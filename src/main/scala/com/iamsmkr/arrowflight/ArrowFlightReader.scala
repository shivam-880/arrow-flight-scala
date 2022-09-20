package com.iamsmkr.arrowflight

import org.apache.arrow.flight._
import org.apache.arrow.memory.BufferAllocator
import java.nio.charset.StandardCharsets
import scala.util.control.NonFatal

case class ArrowFlightReader(
                              interface: String,
                              port: Int,
                              allocator: BufferAllocator
                            ) {

  private val location: Location = Location.forGrpcInsecure(interface, port)
  private val flightClient = FlightClient.builder(allocator, location).build

  def readMessages(busyWaitInMilliSeconds: Long): Unit = {
    while (true) {
      try Thread.sleep(busyWaitInMilliSeconds)
      catch {
        case e: InterruptedException =>
          e.printStackTrace()
      }

      try readMessages()
      catch {
        case NonFatal(e) => e.printStackTrace()
      }
    }
  }

  var counter = 0

  def readMessages(): Unit = {
    val flightInfoIter = flightClient.listFlights(Criteria.ALL)
    if (flightInfoIter.iterator().hasNext) {
      flightInfoIter.forEach { flightInfo =>
        val endPointAsByteStream = flightInfo.getDescriptor.getPath.get(0).getBytes(StandardCharsets.UTF_8)
        val flightStream = flightClient.getStream(new Ticket(endPointAsByteStream))

        val vectorSchemaRootReceived = flightStream.getRoot

        while (flightStream.next()) {
          var i = 0
          val rows = vectorSchemaRootReceived.getRowCount
          while (i < rows) {
            counter = counter + 1
            i = i + 1
          }

          // println("Client Received Data:")
          // println(vectorSchemaRootReceived.contentToTSVString())
        }

        println(s"Total messages read = $counter")
      }
    }
  }
}
