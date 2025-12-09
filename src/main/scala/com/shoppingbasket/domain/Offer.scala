package com.shoppingbasket.domain

/** Discount applied to the basket. Amount is in pence. */
case class Discount(description: String, amount: Int)

/** Sealed trait for special offers. Each implementation calculates discounts for a basket. */
sealed trait Offer {
  def apply(basket: Basket): List[Discount]

  protected def formatDiscountDescription(
    productName: String,
    percentage: Int,
    discountAmount: Int
  ): String =
    s"$productName $percentage% off: ${MoneyFormatter.format(discountAmount)}"
}

/** Percentage discount on a specific product. */
case class PercentageDiscount(productName: String, percentage: Int) extends Offer {
  override def apply(basket: Basket): List[Discount] = {
    val matchingProducts = basket.getProducts(productName)
    if (matchingProducts.isEmpty) {
      Nil
    } else {
      val quantity = matchingProducts.length
      val product = matchingProducts.head
      val discountAmount = Math.round((product.price * quantity * percentage) / 100.0).toInt

      if (discountAmount > 0) {
        List(
          Discount(
            description = formatDiscountDescription(product.name, percentage, discountAmount),
            amount = discountAmount
          )
        )
      } else {
        Nil
      }
    }
  }
}

/** Buy X of one product, get discount on another product. */
case class BuyXGetYDiscount(
  buyProduct: String,
  buyQuantity: Int,
  getProduct: String,
  discountPercentage: Int
) extends Offer {
  override def apply(basket: Basket): List[Discount] = {
    val totalBuyQuantity = basket.getQuantity(buyProduct)

    if (totalBuyQuantity >= buyQuantity) {
      val offerApplications = totalBuyQuantity / buyQuantity
      val getProducts = basket.getProducts(getProduct)
      if (getProducts.isEmpty) {
        Nil
      } else {
        val totalGetQuantity = getProducts.length
        val product = getProducts.head

        // Discount is limited by whichever is smaller: available offers or discounted product
        // quantity. E.g., 4 Soups (2 offers) + 3 Bread → discount 2 Bread (limited by offers).
        // E.g., 4 Soups (2 offers) + 1 Bread → discount 1 Bread (limited by quantity).
        val discountedQuantity = Math.min(offerApplications, totalGetQuantity)

        val discountPerItem = Math.round((product.price * discountPercentage) / 100.0).toInt
        val totalDiscount = discountPerItem * discountedQuantity

        if (totalDiscount > 0) {
          List(
            Discount(
              description =
                formatDiscountDescription(product.name, discountPercentage, totalDiscount),
              amount = totalDiscount
            )
          )
        } else {
          Nil
        }
      }
    } else {
      Nil
    }
  }
}
