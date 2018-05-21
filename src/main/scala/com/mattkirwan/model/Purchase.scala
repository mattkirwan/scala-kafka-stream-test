package com.mattkirwan.model

case class Purchase(ccn: String)

object Purchase {
  def maskCreditCardNumber(ccn: String): String = {
    val last = ccn takeRight 4
    "****-****-****-" + last
  }
}