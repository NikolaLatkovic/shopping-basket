package com.shoppingbasket.data

import com.shoppingbasket.domain.{BuyXGetYDiscount, Offer, PercentageDiscount}
import ProductCatalog.ProductNames

/**
 * Registry of all active special offers. Use ProductCatalog.ProductNames constants for product
 * identifiers.
 */
object OfferRegistry {

  val activeOffers: List[Offer] = List(
    // Apples have a 10% discount off their normal price this week
    PercentageDiscount(ProductNames.Apples, 10),

    // Buy 2 tins of soup and get a loaf of bread for half price
    BuyXGetYDiscount(
      buyProduct = ProductNames.Soup,
      buyQuantity = 2,
      getProduct = ProductNames.Bread,
      discountPercentage = 50
    )
  )
}
