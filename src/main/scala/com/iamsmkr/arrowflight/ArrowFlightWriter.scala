package com.iamsmkr.arrowflight

import org.apache.arrow.memory.BufferAllocator
import org.apache.arrow.flight._
import org.apache.arrow.vector._
import org.apache.arrow.vector.types.pojo._
import scala.jdk.CollectionConverters._

class ArrowFlightWriter(
                         interface: String,
                         port: Int,
                         allocator: BufferAllocator
                       ) {

  private val location = Location.forGrpcInsecure(interface, port)
  private val flightClient = FlightClient.builder(allocator, location).build()
  private val schema = new Schema(List(new Field("name", FieldType.nullable(new ArrowType.Utf8()), null)).asJava)
  private val vectorSchemaRoot = VectorSchemaRoot.create(schema, allocator)
  private val listener = flightClient.startPut(FlightDescriptor.path("profiles"), vectorSchemaRoot, new AsyncPutListener())
  private val varCharVector = vectorSchemaRoot.getVector("name").asInstanceOf[VarCharVector]

  def addToBatch(row: Int, message: String): Unit = {
    varCharVector.setSafe(row, message.getBytes)
  }

  def sendBatch(): Unit = {
    listener.putNext()
  }

  def completeSend(): Unit = {
    listener.completed()
    listener.getResult()
  }

  def close(): Unit = {

  }
}
