package com.iamsmkr.arrowflight

import org.apache.arrow.memory.RootAllocator

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends App {

  val allocator = new RootAllocator

  private val server = ArrowFlightServer(allocator)
  server.waitForServerToStart()

  private val interface = server.getInterface
  private val port = server.getPort

  private val reader = ArrowFlightReader(interface, port, allocator)
  private val writer = ArrowFlightWriter(interface, port, allocator)

  Future(reader.readMessages(1))

  val start = System.currentTimeMillis()

  for (i <- 0 until 10) {
    for (j <- 0 until 10) {
      writer.addToBatch(j, System.currentTimeMillis().toString)
    }
    writer.sendBatch()
  }
  writer.completeSend()

  println(s"Time taken = ${System.currentTimeMillis() - start}")
}
