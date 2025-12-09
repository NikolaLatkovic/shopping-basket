package com.shoppingbasket.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.shoppingbasket.domain.{Basket, Receipt}
import com.shoppingbasket.data.{OfferRegistry, ProductCatalog}

/** Unit tests for ShoppingService */
class ShoppingServiceSpec extends AnyFlatSpec with Matchers {

  "ShoppingService" should "calculate subtotal correctly with no offers (Scenario 1)" in {
    val productNames = List("Milk")
    info(s"Testing with products: ${productNames.mkString(", ")}")
    val products = ShoppingService.validateProducts(productNames) match {
      case Right(prods) => prods
      case Left(invalid) => fail(s"Expected valid products but got: $invalid")
    }
    val basket = Basket(products)
    val discounts = OfferRegistry.activeOffers.flatMap(_.apply(basket))
    val receipt = Receipt(basket, discounts)

    info(s"Subtotal: ${receipt.subtotal}p (expected: 130p)")
    receipt.subtotal shouldBe 130
    receipt.discounts shouldBe empty
    receipt.total shouldBe 130
    receipt.format should include("Subtotal: £1.30")
    receipt.format should include("(No offers available)")
  }

  it should "apply percentage discount correctly (Scenario 2)" in {
    val productNames = List("Apples", "Milk", "Bread")
    info(s"Testing with products: ${productNames.mkString(", ")}")
    val products = ShoppingService.validateProducts(productNames) match {
      case Right(prods) => prods
      case Left(invalid) => fail(s"Expected valid products but got: $invalid")
    }
    val basket = Basket(products)
    val discounts = OfferRegistry.activeOffers.flatMap(_.apply(basket))
    val receipt = Receipt(basket, discounts)

    info(s"Subtotal: ${receipt.subtotal}p (expected: 310p)")
    receipt.subtotal shouldBe 310
    receipt.discounts should have size 1
    info(s"Discount: ${receipt.discounts.head.description} (amount: ${receipt.discounts.head.amount}p)")
    receipt.discounts.head.amount shouldBe 10
    receipt.discounts.head.description should include("Apples 10% off")
    receipt.total shouldBe 300
  }

  it should "apply conditional discount correctly (Scenario 3) - 2 Soup + 1 Bread" in {
    val productNames = List("Soup", "Soup", "Bread")
    info(s"Testing with products: ${productNames.mkString(", ")}")
    info("Expected: 1 offer (2 Soups) matches 1 Bread → discount 1 Bread")
    val products = ShoppingService.validateProducts(productNames) match {
      case Right(prods) => prods
      case Left(invalid) => fail(s"Expected valid products but got: $invalid")
    }
    val basket = Basket(products)
    val discounts = OfferRegistry.activeOffers.flatMap(_.apply(basket))
    val receipt = Receipt(basket, discounts)

    info(s"Subtotal: ${receipt.subtotal}p (expected: 210p)")
    receipt.subtotal shouldBe 210
    receipt.discounts should have size 1
    info(s"Discount: ${receipt.discounts.head.description} (amount: ${receipt.discounts.head.amount}p)")
    receipt.discounts.head.amount shouldBe 40
    receipt.discounts.head.description should include("Bread 50% off")
    receipt.total shouldBe 170
  }

  it should "apply conditional discount when offers exceed discounted products (Scenario 3b) - 4 Soup + 1 Bread" in {
    val productNames = List("Soup", "Soup", "Soup", "Soup", "Bread")
    info(s"Testing with products: ${productNames.mkString(", ")}")
    info("Expected: 2 offers (4 Soups) but only 1 Bread → discount 1 Bread (limited by quantity)")
    val products = ShoppingService.validateProducts(productNames) match {
      case Right(prods) => prods
      case Left(invalid) => fail(s"Expected valid products but got: $invalid")
    }
    val basket = Basket(products)
    val discounts = OfferRegistry.activeOffers.flatMap(_.apply(basket))
    val receipt = Receipt(basket, discounts)

    info(s"Subtotal: ${receipt.subtotal}p (expected: 340p)")
    receipt.subtotal shouldBe 340
    receipt.discounts should have size 1
    info(s"Discount: ${receipt.discounts.head.description} (amount: ${receipt.discounts.head.amount}p)")
    receipt.discounts.head.amount shouldBe 40
    receipt.total shouldBe 300
  }

  it should "apply conditional discount when discounted products exceed offers (Scenario 3c) - 4 Soup + 3 Bread" in {
    val productNames = List("Soup", "Soup", "Soup", "Soup", "Bread", "Bread", "Bread")
    info(s"Testing with products: ${productNames.mkString(", ")}")
    info("Expected: 2 offers (4 Soups) but 3 Bread available → discount 2 Bread (limited by offers)")
    val products = ShoppingService.validateProducts(productNames) match {
      case Right(prods) => prods
      case Left(invalid) => fail(s"Expected valid products but got: $invalid")
    }
    val basket = Basket(products)
    val discounts = OfferRegistry.activeOffers.flatMap(_.apply(basket))
    val receipt = Receipt(basket, discounts)

    info(s"Subtotal: ${receipt.subtotal}p (expected: 500p)")
    receipt.subtotal shouldBe 500
    receipt.discounts should have size 1
    info(s"Discount: ${receipt.discounts.head.description} (amount: ${receipt.discounts.head.amount}p)")
    receipt.discounts.head.amount shouldBe 80
    receipt.total shouldBe 420
  }

  it should "apply multiple offers together (Scenario 4) - Apples + 2 Soup + Bread" in {
    val productNames = List("Apples", "Soup", "Soup", "Bread")
    info(s"Testing with products: ${productNames.mkString(", ")}")
    info("Expected: Both Apples 10% off and Bread 50% off (2 Soup) should apply")
    val products = ShoppingService.validateProducts(productNames) match {
      case Right(prods) => prods
      case Left(invalid) => fail(s"Expected valid products but got: $invalid")
    }
    val basket = Basket(products)
    val discounts = OfferRegistry.activeOffers.flatMap(_.apply(basket))
    val receipt = Receipt(basket, discounts)

    info(s"Subtotal: ${receipt.subtotal}p (expected: 310p)")
    receipt.subtotal shouldBe 310
    receipt.discounts should have size 2
    info(s"Number of discounts: ${receipt.discounts.size} (expected: 2)")
    val applesDiscount = receipt.discounts.find(_.description.contains("Apples"))
    applesDiscount shouldBe defined
    applesDiscount.get.amount shouldBe 10
    val breadDiscount = receipt.discounts.find(_.description.contains("Bread"))
    breadDiscount shouldBe defined
    breadDiscount.get.amount shouldBe 40
    receipt.total shouldBe 260

    val formatted = receipt.format
    info(s"Formatted receipt includes both discounts: ${formatted.contains("Apples 10% off") && formatted.contains("Bread 50% off")}")
    formatted should include("Apples 10% off")
    formatted should include("Bread 50% off")
    formatted should not include "(No offers available)"
  }

  it should "handle edge cases: invalid products, empty basket, case-insensitive matching, and utility functions (Scenario 5)" in {
    // Test 5a: Invalid product name
    info("Test 5a: Invalid product name")
    val invalidProductNames = List("InvalidProduct", "Milk")
    info(s"Testing with products: ${invalidProductNames.mkString(", ")}")
    val invalidResult = ShoppingService.validateProducts(invalidProductNames)
    info(s"Validation result: ${invalidResult.isLeft} (expected: Left with invalid products)")
    invalidResult shouldBe Left(List("InvalidProduct"))
    info("")

    // Test 5b: Empty basket
    info("Test 5b: Empty basket")
    val emptyProducts = ShoppingService.validateProducts(List.empty)
    emptyProducts shouldBe Right(Nil)
    val emptyBasket = Basket(emptyProducts match {
      case Right(prods) => prods
      case Left(invalid) => fail(s"Expected valid products but got: $invalid")
    })
    val emptyDiscounts = OfferRegistry.activeOffers.flatMap(_.apply(emptyBasket))
    val emptyReceipt = Receipt(emptyBasket, emptyDiscounts)
    info(s"Subtotal: ${emptyReceipt.subtotal}p (expected: 0p)")
    emptyReceipt.subtotal shouldBe 0
    emptyReceipt.discounts shouldBe empty
    emptyReceipt.total shouldBe 0
    info("")

    // Test 5c: Case-insensitive product matching
    info("Test 5c: Case-insensitive product matching")
    val caseProductNames = List("apples", "APPLES", "Apples", "MILK")
    info(s"Testing with products: ${caseProductNames.mkString(", ")}")
    val caseProducts = ShoppingService.validateProducts(caseProductNames)
    val caseBasket = Basket(caseProducts match {
      case Right(prods) => prods
      case Left(invalid) => fail(s"Expected valid products but got: $invalid")
    })
    val caseDiscounts = OfferRegistry.activeOffers.flatMap(_.apply(caseBasket))
    val caseReceipt = Receipt(caseBasket, caseDiscounts)
    info(s"Subtotal: ${caseReceipt.subtotal}p (expected: 430p)")
    caseReceipt.subtotal shouldBe 430
    caseReceipt.discounts should have size 1
    caseReceipt.discounts.head.amount shouldBe 30
    caseReceipt.total shouldBe 400
    info("")

    // Test 5d: ProductCatalog utility functions
    info("Test 5d: ProductCatalog utility functions")
    val productNames = ProductCatalog.getAllProductNames
    productNames should contain allOf("Soup", "Bread", "Milk", "Apples")
    productNames should have size 4
    
    val product1 = ProductCatalog.getProduct("  SOUP  ")
    val product2 = ProductCatalog.getProduct("soup")
    product1 shouldBe product2
    product1 shouldBe defined
    product1.get.name shouldBe "Soup"
    info("")

    // Test 5e: MoneyFormatter formatting
    info("Test 5e: MoneyFormatter formatting")
    import com.shoppingbasket.domain.MoneyFormatter
    MoneyFormatter.format(65) shouldBe "65p"
    MoneyFormatter.format(130) shouldBe "£1.30"
    info("")

    // Test 5f: Offer edge cases - missing branches
    info("Test 5f: Offer edge cases for branch coverage")
    import com.shoppingbasket.domain.{PercentageDiscount, BuyXGetYDiscount}
    
    // PercentageDiscount: product not in basket
    val milkBasket = Basket(List(ProductCatalog.getProduct("Milk").get))
    PercentageDiscount("Apples", 10).apply(milkBasket) shouldBe empty
    
    // BuyXGetYDiscount: not enough buyProduct
    val soupBreadBasket = Basket(List(
      ProductCatalog.getProduct("Soup").get,
      ProductCatalog.getProduct("Bread").get
    ))
    BuyXGetYDiscount("Soup", 2, "Bread", 50).apply(soupBreadBasket) shouldBe empty
    
    // BuyXGetYDiscount: getProduct not in basket
    val soupSoupBasket = Basket(List(
      ProductCatalog.getProduct("Soup").get,
      ProductCatalog.getProduct("Soup").get
    ))
    BuyXGetYDiscount("Soup", 2, "Bread", 50).apply(soupSoupBasket) shouldBe empty
    
    // PercentageDiscount: 0% discount (discountAmount = 0 branch)
    PercentageDiscount("Milk", 0).apply(milkBasket) shouldBe empty
    
    // BuyXGetYDiscount: 0% discount (totalDiscount = 0 branch)
    val soupSoupBreadBasket = Basket(List(
      ProductCatalog.getProduct("Soup").get,
      ProductCatalog.getProduct("Soup").get,
      ProductCatalog.getProduct("Bread").get
    ))
    BuyXGetYDiscount("Soup", 2, "Bread", 0).apply(soupSoupBreadBasket) shouldBe empty
  }

  it should "test ShoppingService.processBasket with valid products" in {
    info("Test: ShoppingService.processBasket with valid products")
    val productNames = List("Apples", "Soup", "Soup", "Bread")

    val output = new java.io.ByteArrayOutputStream()
    Console.withOut(output) {
      ShoppingService.processBasket(productNames)
    }
    
    val outputString = output.toString
    info(s"Output captured: \n${outputString.take(100)}...")
    outputString should include("Subtotal:")
    outputString should include("Apples 10% off")
    outputString should include("Bread 50% off")
    outputString should include("Total price:")
  }

}
