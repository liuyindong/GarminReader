package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.io.*;

import javax.usb.UsbStringDescriptor;

/**
 * UsbStringDescriptor implementation.
 * @author Dan Streetman
 */
public class UsbStringDescriptorImp extends UsbDescriptorImp implements UsbStringDescriptor
{
	/**
	 * Constructor.
	 * @param bLength This descriptor's bLength.
	 * @param bDescriptorType This descriptor's bDescriptorType.
	 * @param bString This descriptor's bString.
	 */
	public UsbStringDescriptorImp( byte bLength, byte bDescriptorType, byte[] bString )
	{
		super(bLength, bDescriptorType);
		this.bString = bString;
	}

	/**
	 * Get this descriptor's bString.
	 * @return This descriptor's bString.
	 */
	public byte[] bString()
	{
		try {
			byte[] bStringCopy = new byte[bString.length];

			System.arraycopy(bString, 0, bStringCopy, 0, bString.length);

			return bStringCopy;
		} catch ( NullPointerException npE ) {
			return null;
		}
	}

	/**
	 * Get this descriptor's translated String.
	 * <p>
	 * This is the String translation of the {@link #bString() bString}.
	 * The best available 16-bit encoding is used.  If no 16-bit encoding is available,
	 * 8-bit encoding is used, unless any of the characters are 16 bit (high byte is non-zero);
	 * then 8-bit encoding is not used, and an UnsupportedEncodingException is thrown.
	 * @return This descriptor's String.
	 * @exception UnsupportedEncodingException If no encodings are available.
	 */
    public String getString() throws UnsupportedEncodingException
	{
		if (null == string)
			string = createString();

		return string;
	}

	/**
	 * Compare this to an Object.
	 * @param object The Object to compare to.
	 * @return If this is equal to the Object.
	 */
	public boolean equals(Object object)
	{
		if (!super.equals(object))
			return false;

		if (this == object)
			return true;

		UsbStringDescriptorImp desc = null;

		try { desc = (UsbStringDescriptorImp)object; }
		catch ( ClassCastException ccE ) { return false; }

		try {
			if (getString() != desc.getString())
				return false;
		} catch ( UnsupportedEncodingException ueE ) {
			/* This doesn't mean they're not equal... */
		}

		return
			bString() == desc.bString();
	}

	/**
	 * Create a String for the bString.
	 * @return A String for the bString.
	 * @exception UnsupportedEncodingException If no encodings are available.
	 */
	private String createString() throws UnsupportedEncodingException
	{
		if (null == bString())
			return null;

		byte[] s16 = bString();

		for (int i=0; i<ENCODING.length; i++) {
			try { return new String( s16, 0, s16.length, ENCODING[i] ); }
			catch ( UnsupportedEncodingException ueE ) { }
		}

		/* Fallback to 8BIT encoding */
		byte[] s8 = new byte[s16.length/2];

		/* Convert 16-bit (little-endian) to 8-bit, checking each high bit. */
		for (int i8=0, i16=0; i8<s8.length && (i16+1)<s16.length; i8++, i16++, i16++) {
			s8[i8] = s16[i16];
			/* if high bit is non-zero, character is not 8-bit. */
			if (0 != s16[i16+1])
				throw new UnsupportedEncodingException("No 16-bit encoding available for 16-bit string");
		}

		return new String( s8, ENCODING_8BIT );
	}

	private byte[] bString = null;
	private String string = null;

	/**
	 * These are the encodings used to decode the USB String Descriptors.
	 * <p>
	 * For all encodings supported by Java, see:
	 * <p><a href="http://java.sun.com/products/jdk/1.1/docs/guide/intl/encoding.doc.html">Java 1 (1.1) Supported Encodings</a>
	 * <p><a href="http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html">Java 2 (1.3) Supported Encodings</a>
	 * <p><a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">Java 2 (1.3) Required Encodings</a>
	 * <p>
	 * The translation is done using the first available of these encodings:
	 * <ul>
	 * <li>UnicodeLittleUnmarked</li>
	 * <li>UnicodeLittle</li>
	 * <li>UTF-16LE</li>
	 * <li>ASCII (after conversion from 16 bit to 8 bit)</li>
	 * </ul>
	 */
	public static final String[] ENCODING = {
		"UnicodeLittleUnmarked", /* Present in Sun Java 1.3 rt.jar (not 1.1) */
		"UnicodeLittle", /* Present in Sun Java 1.3 rt.jar and Sun Java 1.1 i18n.jar */
		"UTF-16LE", /* Required by Sun Java 1.3 Package Specifications */
	};
	/** Fallback encoding if no 16-bit encoding is supported */
	public static final String ENCODING_8BIT = "ASCII"; /* Present in Sun Java 1.3 rt.jar and Sun Java 1.1 i18n.jar */
}
