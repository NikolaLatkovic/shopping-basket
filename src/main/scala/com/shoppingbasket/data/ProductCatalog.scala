package com.shoppingbasket.data

import com.shoppingbasket.domain.Product

/** Central place to define all available products and their prices. */
object ProductCatalog {

  private def pence(amount: Int): Int = amount
  private def pounds(amount: Double): Int = (amount * 100).toInt

  /** Product name constants - single source of truth for product identifiers. */
  object ProductNames {
    val Soup = "soup"
    val Bread = "bread"
    val Milk = "milk"
    val Apples = "apples"
  }

  private val products: Map[String, Product] = Map(
    ProductNames.Soup -> Product("Soup", pence(65)),
    ProductNames.Bread -> Product("Bread", pence(80)),
    ProductNames.Milk -> Product("Milk", pounds(1.30)),
    ProductNames.Apples -> Product("Apples", pounds(1.00))
  )

  def getProduct(name: String): Option[Product] =
    products.get(name.trim.toLowerCase)

  def getAllProductNames: List[String] =
    products.values.map(_.name).toList
}
