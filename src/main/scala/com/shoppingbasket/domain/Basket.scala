package com.shoppingbasket.domain

/** Product with price in pence. */
case class Product(name: String, price: Int)

/** Shopping basket. Quantities are calculated automatically from the product list. */
case class Basket(products: List[Product]) {

  /** Subtotal before discounts (in pence). */
  def subtotal: Int = products.map(_.price).sum

  def getProducts(productName: String): List[Product] =
    products.filter(_.name.equalsIgnoreCase(productName))

  def getQuantity(productName: String): Int =
    products.count(_.name.equalsIgnoreCase(productName))
}
