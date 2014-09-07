package myfirstmacro

import org.scalatest.{Matchers, FlatSpec}

/**
 * Author: chris
 * Created: 9/7/14
 */
class TranslationSpec extends FlatSpec with Matchers {

  behavior of "Translated text"

  it should "be in English" in {
    val translated = Translation.toEnglish("こんにちは")

    translated should be("hello")
  }
}
