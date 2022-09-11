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

  Future(reader.readMessages(1000))

  writer.addToBatch(0, "Shivam")
  writer.addToBatch(1, "Shyam")
  writer.addToBatch(2, "Srinivas")
  writer.sendBatch()
  writer.completeSend()
}
