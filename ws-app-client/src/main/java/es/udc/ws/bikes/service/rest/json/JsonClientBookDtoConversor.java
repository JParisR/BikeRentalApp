package es.udc.ws.bikes.service.rest.json;

import java.io.InputStream;
import java.util.Calendar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.bikes.service.dto.ClientBookDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonClientBookDtoConversor {

	public static ClientBookDto toClientBookDto(InputStream jsonBook) throws ParsingException {
		try {

			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonBook);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				ObjectNode bikeObject = (ObjectNode) rootNode;

				JsonNode bookIdNode = bikeObject.get("bookId");
				Long bookId = (bookIdNode != null) ? bookIdNode.longValue() : null;

				Long bikeId = bikeObject.get("bikeId").longValue();
				String email = bikeObject.get("email").textValue();
				String creditCard = bikeObject.get("creditCard").textValue();
				JsonNode startDateNode = bikeObject.get("startDate");
				Calendar startDate = getDate(startDateNode);
				JsonNode endDateNode = bikeObject.get("endDate");
				Calendar endDate = getDate(endDateNode);
				int units = bikeObject.get("units").intValue();

				return new ClientBookDto(bookId, bikeId, email, creditCard, startDate, endDate, units);

			}
		} catch (ParsingException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

	private static Calendar getDate(JsonNode dateNode) {

		if (dateNode == null) {
			return null;
		}
		int day = dateNode.get("day").intValue();
		int month = dateNode.get("month").intValue();
		int year = dateNode.get("year").intValue();
		Calendar releaseDate = Calendar.getInstance();

		releaseDate.set(Calendar.DAY_OF_MONTH, day);
		releaseDate.set(Calendar.MONTH, month - 1);
		releaseDate.set(Calendar.YEAR, year);

		return releaseDate;

	}

}