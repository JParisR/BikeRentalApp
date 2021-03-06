package es.udc.ws.bikes.restservice.servlets;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.udc.ws.bikes.dto.ServiceBikeDto;
import es.udc.ws.bikes.model.bike.Bike;
import es.udc.ws.bikes.model.bikeservice.BikeServiceFactory;
import es.udc.ws.bikes.model.bikeservice.exceptions.InvalidStartDateException;
import es.udc.ws.bikes.model.bikeservice.exceptions.InvalidStartDateToUpdateException;
import es.udc.ws.bikes.restservice.json.JsonServiceExceptionConversor;
import es.udc.ws.bikes.restservice.json.JsonServiceBikeDtoConversor;
import es.udc.ws.bikes.serviceutil.BikeToBikeDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.exceptions.ParsingException;
import es.udc.ws.util.servlet.ServletUtils;

@SuppressWarnings("serial")
public class BikeServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = ServletUtils.normalizePath(req.getPathInfo());
		if (path != null && path.length() > 0) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
					JsonServiceExceptionConversor.toInputValidationException(
							new InputValidationException("Invalid Request: " + "invalid path " + path)),
					null);
			return;
		}
		ServiceBikeDto bikeDto;
		try {
			bikeDto = JsonServiceBikeDtoConversor.toServiceBikeDto(req.getInputStream());
		} catch (ParsingException ex) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST, JsonServiceExceptionConversor
					.toInputValidationException(new InputValidationException(ex.getMessage())), null);
			return;
		}
		Bike bike = BikeToBikeDtoConversor.toBike(bikeDto);
		try {
			bike = BikeServiceFactory.getService().addBike(bike);
		} catch (InputValidationException ex) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
					JsonServiceExceptionConversor.toInputValidationException(ex), null);
			return;
		} catch (InvalidStartDateException ex) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST, 
					JsonServiceExceptionConversor.toInvalidStartDateException(ex), null);
			return;
		}
		bikeDto = BikeToBikeDtoConversor.toBikeDto(bike);

		String bikeURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + bike.getBikeId();
		Map<String, String> headers = new HashMap<>(1);
		headers.put("Location", bikeURL);

		ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
				JsonServiceBikeDtoConversor.toObjectNode(bikeDto), headers);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String path = ServletUtils.normalizePath(req.getPathInfo());
		if (path == null || path.length() == 0) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST, JsonServiceExceptionConversor
					.toInputValidationException(new InputValidationException("Invalid Request: " + "invalid bike id")),
					null);
			return;
		}
		String bikeIdAsString = path.substring(1);
		Long bikeId;
		try {
			bikeId = Long.valueOf(bikeIdAsString);
		} catch (NumberFormatException ex) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
					JsonServiceExceptionConversor.toInputValidationException(new InputValidationException(
							"Invalid Request: " + "invalid bike id '" + bikeIdAsString + "'")),
					null);
			return;
		}

		ServiceBikeDto bikeDto;
		try {
			bikeDto = JsonServiceBikeDtoConversor.toServiceBikeDto(req.getInputStream());
		} catch (ParsingException ex) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST, JsonServiceExceptionConversor
					.toInputValidationException(new InputValidationException(ex.getMessage())), null);
			return;

		}
		Bike bike = BikeToBikeDtoConversor.toBike(bikeDto);
		bike.setBikeId(bikeId);
		try {
			BikeServiceFactory.getService().updateBike(bike);
		} catch (InputValidationException ex) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
					JsonServiceExceptionConversor.toInputValidationException(ex), null);
			return;
		} catch (InstanceNotFoundException ex) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
					JsonServiceExceptionConversor.toInstanceNotFoundException(ex), null);
			return;
		} catch (InvalidStartDateToUpdateException ex) {
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
					JsonServiceExceptionConversor.toInvalidStartDateToUpdateException(ex), null);
			return;
		}
		ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = ServletUtils.normalizePath(req.getPathInfo());
		List<Bike> bikes;
		List<ServiceBikeDto> bikeDtos;
		if (path == null || path.length() == 0) {
			try {
				String keywords = req.getParameter("keywords");
				String strStartDate = req.getParameter("startDate");
				if (keywords == null || strStartDate == null) {
					throw new InputValidationException("Wrong parameters.");
				}
				Calendar startDate = calendarFromString(strStartDate);
				bikes = BikeServiceFactory.getService().findBikesByKeywords(keywords, startDate);
			
				bikeDtos = BikeToBikeDtoConversor.toBikeDtos(bikes);
				ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
						JsonServiceBikeDtoConversor.toArrayNode(bikeDtos), null);
			} catch (InputValidationException ex) {
				ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
						JsonServiceExceptionConversor.toInputValidationException(ex), null);
				return;
			}
		} else {
			String bikeIdStr = path.substring(1);
			Long bikeId = Long.valueOf(bikeIdStr);
			Bike bike;
			try {
				bike = BikeServiceFactory.getService().findBike(bikeId);
				ServiceBikeDto bikeDto = BikeToBikeDtoConversor.toBikeDto(bike);
				ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
						JsonServiceBikeDtoConversor.toObjectNode(bikeDto), null);
			} catch (InstanceNotFoundException e) {
				ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND, JsonServiceExceptionConversor
						.toInstanceNotFoundException(e), null);
			}
		}
	}
	
	private static Calendar calendarFromString(String date) {
		String[] strDate = date.split("-");
		Calendar cDate = Calendar.getInstance();
		cDate.clear();
		cDate.set(Calendar.DAY_OF_MONTH, Integer.valueOf(strDate[0]));
		cDate.set(Calendar.MONTH, Integer.valueOf(strDate[1]) - 1);
		cDate.set(Calendar.YEAR, Integer.valueOf(strDate[2]));
		
		return cDate;
	}
	
}
