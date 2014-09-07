package myfirstmacro

// マクロ機能を明示的に有効にする
import scala.language.experimental.macros
import scala.util.Success

// ブラックボックスとホワイトボックスという２種類のマクロがある。
// ホワイトのほうが有力だけど、今回はブラックが充分。
import scala.reflect.macros.blackbox.Context

// API呼び出し、JSONパースのためにRaptureを使う
// (not sure exactly which Rapture imports are needed)
import rapture.uri._
import rapture.io._
import rapture.net._
import rapture.json._
import rapture.json.jsonParsers.jackson._

/**
 * Author: chris
 * Created: 9/7/14
 */
object Translation {

  def toEnglish(text: String): String = macro toEnglishImpl

  def toEnglishImpl(c: Context)(text: c.Tree) = {
    import c.universe._

    // 渡された文字列を抽出
    val input: String = text match {
      case Literal(Constant(string)) => string.asInstanceOf[String]
      case _ => c.abort(c.enclosingPosition, "Sorry, you can only use this macro with literal Strings")
    }
    println(s"Translating $input ...")

    // 翻訳APIを呼び出す
    implicit val encoding = Encodings.`UTF-8`
    val json = {
      import rapture.core.strategy.throwExceptions
      val response = uri"http://glosbe.com/gapi/translate?from=ja&dest=eng&format=json&pretty=true&tm=false&phrase=${input.urlEncode}"
        .slurp[Char]
      Json.parse(response)
    }

    // 最初の翻訳結果を抽出して使う
    val firstResult: scala.util.Try[String] = {
      import rapture.core.strategy.returnTry
      json.tuc(0).phrase.text.as[String]
    }

    // 翻訳結果があれば返す、なければコンパイルエラーを投げる
    firstResult match {
      case Success(translation) =>
        // StringをTreeに変換する
        q"$translation"
      case _ =>
        // 翻訳結果が見つからない
        c.abort(c.enclosingPosition, s"Sorry, couldn't find a translation for $input")
    }
  }

}
