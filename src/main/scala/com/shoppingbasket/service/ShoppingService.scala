package com.shoppingbasket.service

import com.shoppingbasket.domain.{Basket, Product, Receipt}
import com.shoppingbasket.data.{OfferRegistry, ProductCatalog}

/** Service for validating products and processing shopping baskets with offers. */
object ShoppingService {

  /** Validate product names and convert to Products. Returns Left with invalid names if any. */
  def validateProducts(productNames: List[String]): Either[List[String], List[Product]] = {
    val invalidProducts = List.newBuilder[String]
    val validProducts = productNames.flatMap { name =>
      ProductCatalog.getProduct(name) match {
        case Some(product) => Some(product)
        case None =>
          invalidProducts += name
          None
      }
    }

    if (invalidProducts.result().isEmpty) {
      Right(validProducts)
    } else {
      Left(invalidProducts.result().toList)
    }
  }

  /** Process product names, create basket, apply offers, and print receipt. */
  def processBasket(productNames: List[String]): Unit =
    validateProducts(productNames) match {
      case Right(products) =>
        // Create basket
        val basket = Basket(products)
        val offers = OfferRegistry.activeOffers
        // Apply all offers to basket to calculate discounts
        val discounts = offers.flatMap(_.apply(basket))
        val receipt = Receipt(basket, discounts)
        println(receipt.format)

      case Left(invalidProducts) =>
        println(s"Error: Invalid product name(s): ${invalidProducts.mkString(", ")}")
        println(s"Available products: ${ProductCatalog.getAllProductNames.mkString(", ")}")
        sys.exit(1)
    }

}
