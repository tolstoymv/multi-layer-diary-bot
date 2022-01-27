package example

import cats.instances.future._
import cats.syntax.functor._
import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.Commands
import com.bot4s.telegram.clients.FutureSttpClient
import com.bot4s.telegram.future.{Polling, TelegramBot}
import com.bot4s.telegram.methods.SendDice
import sttp.client3._
import sttp.client3.okhttp.OkHttpFutureBackend

import scala.concurrent.Await
import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.duration.Duration

object Hello extends Greeting with App {
  val bot = new RandomBot("")
  val eol = bot.run()
  println("Press [ENTER] to shutdown the bot, it may take a few seconds...")
  scala.io.StdIn.readLine()
  bot.shutdown() // initiate shutdown
  // Wait for the bot end-of-life
  Await.result(eol, Duration.Inf)
}

trait Greeting {
  lazy val greeting: String = "hello"
}

class RandomBot(token: String) extends ExampleBot(token) with Polling with Commands[Future] {

  val rng = new scala.util.Random(System.currentTimeMillis())
  onCommand("coin" or "flip") { implicit msg =>
    reply(if (rng.nextBoolean()) "Head!" else "Tail!").void
  }
  onCommand("real" | "double" | "float") { implicit msg =>
    reply(rng.nextDouble().toString).void
  }
  onCommand("/die" | "roll") { implicit msg =>
    reply("⚀⚁⚂⚃⚄⚅" (rng.nextInt(6)).toString).void
  }
  onCommand("random" or "rnd") { implicit msg =>
    withArgs {
      case Seq(Int(n)) if n > 0 =>
        reply(rng.nextInt(n).toString).void
      case _ => reply("Invalid argumentヽ(ಠ_ಠ)ノ").void
    }
  }
  onCommand("choose" | "pick" | "select") { implicit msg =>
    withArgs { args =>
      replyMd(if (args.isEmpty) "No arguments provided." else args(rng.nextInt(args.size))).void
    }
  }

  onCommand("auto") { implicit msg =>
    request(SendDice(msg.chat.id)).void
  }
  // Extractor
  object Int {
    def unapply(s: String): Option[Int] = Try(s.toInt).toOption
  }

}

import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.clients.FutureSttpClient
import com.bot4s.telegram.future.TelegramBot

import scala.concurrent.Future

/**
 * Quick helper to spawn example bots.
 *
 * Mix Polling or Webhook accordingly.
 *
 * Example:
 * new EchoBot(":").run()
 *
 * @param token Bot's token.
 */
abstract class ExampleBot(val token: String) extends TelegramBot {

  implicit val backend                        = OkHttpFutureBackend()
  override val client: RequestHandler[Future] = new FutureSttpClient(token)
}