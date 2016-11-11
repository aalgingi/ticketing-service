package com.walmart.ticketing.exception;

import org.springframework.util.StringUtils;

public class TicketingServiceException  extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private TicketingServiceErrorCode errorCode;
	
	public TicketingServiceException(TicketingServiceErrorCode errorCode, String errorMessage){
		super(errorMessage);
		this.errorCode = errorCode;
	}
	
	public static TicketingServiceException asTicketingServiceException(Exception e){
		return asTicketingServiceException(e, null, null);
	}
	
	public static TicketingServiceException asTicketingServiceException(Exception e, TicketingServiceErrorCode errorCode, String errorMessage){
		if(e instanceof TicketingServiceException){
			return((TicketingServiceException) e);
		}
		else{
			TicketingServiceErrorCode eCode = (errorCode == null) ? TicketingServiceErrorCode.E_InternalError : errorCode;
			String eMessage = StringUtils.isEmpty((errorCode)) ? "Internal Server Error" : errorMessage;
			return new TicketingServiceException(eCode, eMessage);
		}
	}

	public TicketingServiceErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(TicketingServiceErrorCode errorCode) {
		this.errorCode = errorCode;
	}	
}
