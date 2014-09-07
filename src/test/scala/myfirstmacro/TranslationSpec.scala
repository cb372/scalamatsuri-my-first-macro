package myfirstmacro

import org.scalatest.{Matchers, FlatSpec}

/**
 * Author: chris
 * Created: 9/7/14
 */
class TranslationSpec extends FlatSpec with Matchers {

  behavior of "Translation"

  it should "translate a Japanese string into English" in {
    val translated = Translation.toEnglish("こんにちは")
    translated should be("hello")
  }

  it should "fail to compile if it cannot find an translation" in {
    """Translation.toEnglish("ぶーびーばーぶーぴー")""" shouldNot compile
  }
}
