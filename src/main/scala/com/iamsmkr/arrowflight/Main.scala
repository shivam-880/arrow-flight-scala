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

  for (i <- 0 until 2)
    writer.addToBatch(i, System.currentTimeMillis().toString)
  writer.sendBatch()
  // writer.completeSend() Not needed here if you're sending another batch to the same end point

  for (i <- 0 until 4) {
    Thread.sleep(1000)
    println(s"Adding to batch")
    writer.addToBatch(i, System.currentTimeMillis().toString)
  }
  writer.sendBatch()
  writer.completeSend()
}
