// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg.proto

// Protobuf Java Version: 4.26.1
package com.hibob.anyim.netty.protobuf;

/**
 * Protobuf enum {@code com.hibob.anyim.netty.protobuf.MsgType}
 */
public enum MsgType
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>HELLO = 0;</code>
   */
  HELLO(0),
  /**
   * <code>HEART_BEAT = 1;</code>
   */
  HEART_BEAT(1),
  /**
   * <code>CHAT = 2;</code>
   */
  CHAT(2),
  /**
   * <code>GROUP_CHAT = 3;</code>
   */
  GROUP_CHAT(3),
  /**
   * <code>CLOSE_BY_READ_IDLE = 10;</code>
   */
  CLOSE_BY_READ_IDLE(10),
  /**
   * <code>CLOSE_BY_ERROR_MAGIC = 11;</code>
   */
  CLOSE_BY_ERROR_MAGIC(11),
  /**
   * <code>DEFAULT = 99;</code>
   */
  DEFAULT(99),
  UNRECOGNIZED(-1),
  ;

  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 26,
      /* patch= */ 1,
      /* suffix= */ "",
      MsgType.class.getName());
  }
  /**
   * <code>HELLO = 0;</code>
   */
  public static final int HELLO_VALUE = 0;
  /**
   * <code>HEART_BEAT = 1;</code>
   */
  public static final int HEART_BEAT_VALUE = 1;
  /**
   * <code>CHAT = 2;</code>
   */
  public static final int CHAT_VALUE = 2;
  /**
   * <code>GROUP_CHAT = 3;</code>
   */
  public static final int GROUP_CHAT_VALUE = 3;
  /**
   * <code>CLOSE_BY_READ_IDLE = 10;</code>
   */
  public static final int CLOSE_BY_READ_IDLE_VALUE = 10;
  /**
   * <code>CLOSE_BY_ERROR_MAGIC = 11;</code>
   */
  public static final int CLOSE_BY_ERROR_MAGIC_VALUE = 11;
  /**
   * <code>DEFAULT = 99;</code>
   */
  public static final int DEFAULT_VALUE = 99;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static MsgType valueOf(int value) {
    return forNumber(value);
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   */
  public static MsgType forNumber(int value) {
    switch (value) {
      case 0: return HELLO;
      case 1: return HEART_BEAT;
      case 2: return CHAT;
      case 3: return GROUP_CHAT;
      case 10: return CLOSE_BY_READ_IDLE;
      case 11: return CLOSE_BY_ERROR_MAGIC;
      case 99: return DEFAULT;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<MsgType>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      MsgType> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<MsgType>() {
          public MsgType findValueByNumber(int number) {
            return MsgType.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalStateException(
          "Can't get the descriptor of an unrecognized enum value.");
    }
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return com.hibob.anyim.netty.protobuf.MsgOuterClass.getDescriptor().getEnumTypes().get(0);
  }

  private static final MsgType[] VALUES = values();

  public static MsgType valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private MsgType(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:com.hibob.anyim.netty.protobuf.MsgType)
}
