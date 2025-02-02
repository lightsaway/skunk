// Copyright (c) 2018 by Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package tests

import cats.implicits._
import io.circe.Json
import io.circe.parser.parse
import skunk._
import skunk.circe.codec.all._
import skunk.implicits._

case object JsonCodecTest extends CodecTest {

  val j: Json = parse("""{"foo": [true, "bar"], "tags": {"a": 1, "b": null}}""").toOption.get

  codecTest(json)(j)
  codecTest(json[Int ~ String])(42 ~ "foo")
  codecTest(jsonb)(j)
  codecTest(jsonb[Int ~ String])(42 ~ "foo")

}


