package com.iamsmkr.arrowflight

import org.apache.arrow.flight._
import org.apache.arrow.memory.BufferAllocator
import java.nio.charset.StandardCharsets

case class ArrowFlightReader(
                         interface: String,
                         port: Int,
                         allocator: BufferAllocator
                       ) {

  private val location: Location = Location.forGrpcInsecure(interface, port)
  private val flightClient = FlightClient.builder(allocator, location).build

  def readMessages(busyWaitInMilliSeconds: Long): Unit = {
    while (true) {
      try {
        Thread.sleep(busyWaitInMilliSeconds)
      } catch {
        case e: InterruptedException =>
          e.printStackTrace()
      }

      readMessages()
    }
  }

  def readMessages(): Unit = {
    val flightInfoIter = flightClient.listFlights(Criteria.ALL)
//    if (!flightInfoIter.iterator().hasNext()) throw new Exception("No data found against any endpoint!")

    flightInfoIter.forEach { flightInfo =>
      val endPointAsByteStream = flightInfo.getDescriptor.getPath.get(0).getBytes(StandardCharsets.UTF_8)
      val flightStream = flightClient.getStream(new Ticket(endPointAsByteStream))

      var batch = 0
      val vectorSchemaRootReceived = flightStream.getRoot

      while (flightStream.next()) {
        batch = batch + 1
        println("Client Received batch #" + batch + ", Data:")
        print(vectorSchemaRootReceived.contentToTSVString())
      }
    }

  }
}
