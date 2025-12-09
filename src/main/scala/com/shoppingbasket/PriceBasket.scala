package com.shoppingbasket

import com.shoppingbasket.service.ShoppingService
import com.shoppingbasket.data.ProductCatalog

object PriceBasket {

  def main(args: Array[String]): Unit = {
    // Skip "PriceBasket" if it's the first argument (for JAR execution)
    val productNames = if (args.nonEmpty && args(0).equalsIgnoreCase("PriceBasket")) {
      args.tail.toList
    } else {
      args.toList
    }

    if (productNames.isEmpty) {
      println("Usage: PriceBasket item1 item2 item3 ...")
      println("Example: PriceBasket Apples Milk Bread")
      println(s"Available products: ${ProductCatalog.getAllProductNames.mkString(", ")}")
      sys.exit(1)
    }

    ShoppingService.processBasket(productNames)
  }
}
