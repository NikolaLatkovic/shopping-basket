package com.shoppingbasket.domain

/** Utility for formatting money amounts. Shows pounds when >= 100 pence, otherwise pence. */
object MoneyFormatter {

  def format(amount: Int): String =
    if (amount >= 100) {
      val pounds = amount / 100.0
      s"£${String.format("%.2f", pounds)}"
    } else {
      s"${amount}p"
    }
}

/** Receipt with pricing breakdown. */
case class Receipt(basket: Basket, discounts: List[Discount]) {

  def subtotal: Int = basket.subtotal

  def totalDiscount: Int = discounts.map(_.amount).sum

  def total: Int = Math.max(basket.subtotal - totalDiscount, 0)

  /** Format receipt as string for console output. */
  def format: String = {
    val lines = List.newBuilder[String]

    lines += s"Subtotal: ${MoneyFormatter.format(basket.subtotal)}"

    if (discounts.isEmpty) {
      lines += "(No offers available)"
    } else {
      discounts.foreach { discount =>
        lines += discount.description
      }
    }

    lines += s"Total price: ${MoneyFormatter.format(total)}"

    lines.result().mkString("\n")
  }

}
