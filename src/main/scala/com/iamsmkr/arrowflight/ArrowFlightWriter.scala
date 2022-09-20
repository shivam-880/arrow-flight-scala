package com.iamsmkr.arrowflight

import org.apache.arrow.memory.BufferAllocator
import org.apache.arrow.flight._
import org.apache.arrow.vector._
import org.apache.arrow.vector.types.pojo._
import scala.jdk.CollectionConverters._

case class ArrowFlightWriter(
                         interface: String,
                         port: Int,
                         allocator: BufferAllocator
                       ) {

  private val location = Location.forGrpcInsecure(interface, port)
  private val flightClient = FlightClient.builder(allocator, location).build()
  private val schema = new Schema(List(new Field("name", FieldType.nullable(new ArrowType.Utf8()), null)).asJava)
  private val vectorSchemaRoot = VectorSchemaRoot.create(schema, allocator)
  private var listener = flightClient.startPut(FlightDescriptor.path("profiles"), vectorSchemaRoot, new AsyncPutListener())
  private val varCharVector = vectorSchemaRoot.getVector("name").asInstanceOf[VarCharVector]
  private var lastRow = -1

  def addToBatch(row: Int, message: String): Unit = {
    varCharVector.setSafe(row, message.getBytes)
    lastRow = row
    if (listener == null) listener = flightClient.startPut(FlightDescriptor.path("profiles"), vectorSchemaRoot, new AsyncPutListener())
  }

  def sendBatch(): Unit = {
    vectorSchemaRoot.setRowCount(lastRow + 1)
    listener.putNext()
    lastRow = -1
  }

  def completeSend(): Unit = {
    listener.completed()
    listener.getResult()
    listener = null
    println(s"Completed Send")
  }

  def close(): Unit = {

  }
}
