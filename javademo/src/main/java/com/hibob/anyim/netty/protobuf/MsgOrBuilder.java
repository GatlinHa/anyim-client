// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg.proto

// Protobuf Java Version: 4.26.1
package com.hibob.anyim.netty.protobuf;

public interface MsgOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.hibob.anyim.netty.protobuf.Msg)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.com.hibob.anyim.netty.protobuf.Header header = 1;</code>
   * @return Whether the header field is set.
   */
  boolean hasHeader();
  /**
   * <code>.com.hibob.anyim.netty.protobuf.Header header = 1;</code>
   * @return The header.
   */
  com.hibob.anyim.netty.protobuf.Header getHeader();
  /**
   * <code>.com.hibob.anyim.netty.protobuf.Header header = 1;</code>
   */
  com.hibob.anyim.netty.protobuf.HeaderOrBuilder getHeaderOrBuilder();

  /**
   * <code>.com.hibob.anyim.netty.protobuf.Body body = 2;</code>
   * @return Whether the body field is set.
   */
  boolean hasBody();
  /**
   * <code>.com.hibob.anyim.netty.protobuf.Body body = 2;</code>
   * @return The body.
   */
  com.hibob.anyim.netty.protobuf.Body getBody();
  /**
   * <code>.com.hibob.anyim.netty.protobuf.Body body = 2;</code>
   */
  com.hibob.anyim.netty.protobuf.BodyOrBuilder getBodyOrBuilder();

  /**
   * <code>optional .com.hibob.anyim.netty.protobuf.Extension extension = 99;</code>
   * @return Whether the extension field is set.
   */
  boolean hasExtension();
  /**
   * <code>optional .com.hibob.anyim.netty.protobuf.Extension extension = 99;</code>
   * @return The extension.
   */
  com.hibob.anyim.netty.protobuf.Extension getExtension();
  /**
   * <code>optional .com.hibob.anyim.netty.protobuf.Extension extension = 99;</code>
   */
  com.hibob.anyim.netty.protobuf.ExtensionOrBuilder getExtensionOrBuilder();
}