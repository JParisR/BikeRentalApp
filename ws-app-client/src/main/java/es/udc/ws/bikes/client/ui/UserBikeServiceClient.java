package es.udc.ws.bikes.client.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.udc.ws.bikes.client.service.UserClientBikeService;
import es.udc.ws.bikes.client.service.UserClientBikeServiceFactory;
import es.udc.ws.bikes.client.service.dto.UserClientBikeDto;
import es.udc.ws.bikes.client.service.dto.UserClientBookDto;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class UserBikeServiceClient {

	public final static String CONVERSION_PATTERN = "dd-MM-yyyy";
	
	public static void main(String[] args) {
		
		
		if(args.length == 0) {
            printUsageAndExit();
        }
		SimpleDateFormat dateFormatter = new SimpleDateFormat(CONVERSION_PATTERN, Locale.ENGLISH);
		
		UserClientBikeService clientBikeService = UserClientBikeServiceFactory.getService();
		
		if("-f".equalsIgnoreCase(args[0])) {
            validateArgs(args, 3, new int[] {});

            // [find] bikeServiceClient -f <keywords> <availabilityDate>
                   
            try {
            	
            	List<UserClientBikeDto> bikes;
            	
        		Date date = null;
        		date = dateFormatter.parse(args[2]);
        		Calendar startDate = Calendar.getInstance();
        		startDate.setTime(date);
            		
        		bikes = clientBikeService.findBikes(args[1], startDate);
        		System.out.println("Found " + bikes.size() + " bike(s) with keywords '" + args[1] + "'" + 
        				" and available from " + args[2]);
            
        		for (int i = 0; i < bikes.size(); i++) {
                	UserClientBikeDto bikeDto = bikes.get(i);
                    
                	System.out.print("Name: " + bikeDto.getName());
                	System.out.print(", description: " + bikeDto.getDescription());
                	System.out.print(", availability date: " + stringDate(bikeDto.getStartDate()));
                	if (bikeDto.getNumberOfRates() == 0) {
                		System.out.println(", no rates yet");
                	} else {
                    	System.out.print(", Number of rates: " + bikeDto.getNumberOfRates());
                    	System.out.println(", Mean rate: " + bikeDto.getAvgRate());
                	}
            	}
            	
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
		} else if ("-r".equalsIgnoreCase(args[0])) {

            validateArgs(args, 7, new int[] {2, 6});

            // [reserve] bikeServiceClient -r <email> <bikeId> <creditCardNumber> <startDate> <endDate> <units>

            Long bookId;
            Date dateStart;
            Date dateEnd;
            try {
            	
            	String email = args[1];
         	   	Long bikeId = Long.valueOf(args[2]);
         	   	String creditCard = args[3];
         	   	
            	dateStart = dateFormatter.parse(args[4]);
            	Calendar startDate = Calendar.getInstance();
            	startDate.setTime(dateStart);
            	
            	dateEnd = dateFormatter.parse(args[5]);
            	Calendar endDate = Calendar.getInstance();
            	endDate.setTime(dateEnd);
            	
            	int units = Integer.valueOf(args[6]);
                bookId = clientBikeService.rentBike(new UserClientBookDto(bikeId, email, creditCard, startDate, endDate, units));

                System.out.println("bike " + args[2] +
                        " purchased sucessfully with book number " +
                        bookId);

            } catch (NumberFormatException | InstanceNotFoundException |
                     InputValidationException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
            
		} else if ("-rb".equalsIgnoreCase(args[0])) {
			// [rateBook] -rb <id> <email> <rate>
			validateArgs(args, 4, new int[] {1, 3});
			
			try {
				Long bookId = Long.valueOf(args[1]);
				String email = args[2];
				int rating = Integer.valueOf(args[3]);
			
				clientBikeService.rateBook(bookId, email, rating);
			
				System.out.println("book " + bookId + " rated sucessfully with " + rating + " points.");
		
			} catch (InstanceNotFoundException | InputValidationException ex) {
				ex.printStackTrace(System.err);
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}        
			
		} else if("-fr".equalsIgnoreCase(args[0])) {
        	validateArgs(args, 2, new int[] {});
        	
        	try {
        		
        		List<UserClientBookDto> lista = clientBikeService.findBooks(args[1]);
        		
        		System.out.println("Found " + lista.size() + " reservation(s) with email '"
        				+ args[1] + "'");
        		
        		for(int i = 0 ; i< lista.size();i++) {
        			UserClientBookDto book = lista.get(i);
        			
                	System.out.print("BookId: " + book.getBookId());
                	System.out.print(", bikeId: " + book.getBikeId());
                	System.out.print(", start date: " + stringDate(book.getStartDate()));
                	System.out.print(", days: " + book.getDays());
                	if (book.getRating() != -1) {
                		System.out.println(", rate: " + book.getRating());       
                	} else {
                		System.out.println(", not rated yet");
                	}
                }
        		
        	} catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        	
        }

	}
		
	public static void validateArgs(String[] args, int expectedArgs, int[] numericArguments) {
		if(expectedArgs != args.length) {
			printUsageAndExit();
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(" ")) {
				args[i] = "";
			}
		}
		for(int i = 0 ; i< numericArguments.length ; i++) {
			int position = numericArguments[i];
			try {
				Double.parseDouble(args[position]);
			} catch(NumberFormatException n) {
				printUsageAndExit();
			}
		}
	}
	
	private static String stringDate(Calendar date) {
		return Integer.toString(date.get(Calendar.DAY_OF_MONTH)) + "-" +
				Integer.toString(date.get(Calendar.MONTH) + 1) + "-" +
				Integer.toString(date.get(Calendar.YEAR));
	}

	public static void printUsageAndExit() {
		printUsage();
		System.exit(-1);
	}

	public static void printUsage() {
		System.err.println("Usage:\n" +
				"    [find Books]   bikeserviceClient -fr <email>\n" +
				"    [find Bikes]   bikeserviceClient -f <keywords> <date>\n" +
				"    [reserve]		bikeserviceClient -r <userId> <bikeId> <creditCardNumber> <startDate> <endDate> <units>\n" +
				"    [RateBook]  	bikeserviceClient -rb <bookId> <email> <rate>\n");
	}
}
