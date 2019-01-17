/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ncredinburgh.iata;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.ncredinburgh.iata.model.FlightSegment;
import com.ncredinburgh.iata.model.IataCode;
import com.ncredinburgh.iata.specs.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserTest
{
    @Test(expected = ParseException.class)
    public void testShouldThrowExceptionWhenCodeTooLong() throws ParseException
    {
        new Parser().parse(new String(new char[1000]));
    }

    @Test(expected = ParseException.class)
    public void testShouldThrowExceptionWhenCodeContainsExcessData() throws ParseException
    {
        // Excess data '-EXTRA'
        new Parser().strict().parse("M1DESMARAIS/LUC       EABC123 YULFRAAC 0834 226F001A0025 10001A-EXTRA");
    }

    @Test(expected = ParseException.class)
    public void testShouldThrowExceptionWhenStrictAndDataTypeInvalid() throws ParseException
    {
        new Parser().strict().parse("M1DESMARAIS/LUC       EABC123 000FRAAC 0834 226F001A0025 100");
    }

    @Test(expected = ParseException.class)
    public void testShouldThrowExceptionWhenStrictAndValueInvalid() throws ParseException
    {
        new Parser().strict().parse("M1DESMARAIS/LUC       XABC123 YULFRAAC 0834 226F001A0025 100");
    }

    @Test(expected = ParseException.class)
    public void testShouldThrowExceptionWhenInsufficientData() throws ParseException
    {
        new Parser().strict().parse("M1DESMARAIS/LUC       ");
    }

    @Test(expected = ParseException.class)
    public void testShouldThrowExceptionWhenCodeContainsNegativeNumber() throws ParseException, IOException
    {
        new Parser().parse("M1DESMARAIS/LUC       EABC123 YULFRAAC 0834 226F001A0025 1-1");
    }

    @Test
    public void test1() throws ParseException {
        IataCode iataCode = new Parser().parse("M1LEOPOLD/EMR         EZQ7O92 GVALHRBA 00723319C002F00009100");
        assertEquals("LEOPOLD/EMR", iataCode.getPassengerName());
        List<FlightSegment> flightSegments = iataCode.getFlightSegments();
        FlightSegment flightSegment = flightSegments.get(0);
        assertEquals("00723", flightSegment.getFlightNumber());

        assertEquals(319, flightSegment.getJulianDateOfFlight().intValue());
        Calendar dateOfFlight = flightSegment.getDateOfFlight();
        assertEquals(15, dateOfFlight.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.NOVEMBER, dateOfFlight.get(Calendar.MONTH));
    }

    @Test
    public void test2() throws ParseException {
        IataCode iataCode = new Parser().parse("M1TEST/PETER          E24Z5RN AMSBRUKL 1733 019M008A0001 316>503  W0D0742497067621");
        assertEquals("TEST/PETER", iataCode.getPassengerName());

        List<FlightSegment> flightSegments = iataCode.getFlightSegments();
        FlightSegment flightSegment = flightSegments.get(0);
        assertEquals("1733", flightSegment.getFlightNumber());

        assertEquals(19, flightSegment.getJulianDateOfFlight().intValue());
        Calendar dateOfFlight = flightSegment.getDateOfFlight();
        assertEquals(19, dateOfFlight.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, dateOfFlight.get(Calendar.MONTH));
    }

    @Test
    public void test3() throws ParseException {
        IataCode iataCode = new Parser().parse("M1ASKREN/TEST         EA272SL ORDNRTUA 0881 007F002K0303 15C>3180 M6007BUA              2901624760758980 UA UA EY975897            *30600    09  UAG    ^160MEYCIQCVDy6sskR0zx8Ac5aXCG0hjkejH587woSGHWnbBRbp8QIhAJ790UHbTHG9nZLnllP+JjStGWPLWGR7Ag5on2FPCeRG");
        assertEquals("ASKREN/TEST", iataCode.getPassengerName());

        List<FlightSegment> flightSegments = iataCode.getFlightSegments();
        FlightSegment flightSegment = flightSegments.get(0);
        assertEquals("0881", flightSegment.getFlightNumber());

        assertEquals(7, flightSegment.getJulianDateOfFlight().intValue());
        Calendar dateOfFlight = flightSegment.getDateOfFlight();
        assertEquals(7, dateOfFlight.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, dateOfFlight.get(Calendar.MONTH));

        String dateOfPassIssuance = iataCode.getDateOfPassIssuance();
        assertEquals("6007", dateOfPassIssuance);
    }

    @Test
    public void test4() throws ParseException {
        IataCode iataCode = new Parser().parse("M1TEST/HIDDEN         E8OQ6FU FRARLGLH 4010 012C004D0001 35C>2180WM6012BLH              2922023642241060 LH                        *30600000K09");
        assertEquals("ASKREN/TEST", iataCode.getPassengerName());

        List<FlightSegment> flightSegments = iataCode.getFlightSegments();
        FlightSegment flightSegment = flightSegments.get(0);
        assertEquals("0881", flightSegment.getFlightNumber());

        assertEquals(7, flightSegment.getJulianDateOfFlight().intValue());
        Calendar dateOfFlight = flightSegment.getDateOfFlight();
        assertEquals(7, dateOfFlight.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, dateOfFlight.get(Calendar.MONTH));
        assertEquals(2016, dateOfFlight.get(Calendar.YEAR));

        String dateOfPassIssuance = iataCode.getDateOfPassIssuance();
        assertEquals("6007", dateOfPassIssuance);
    }

}
