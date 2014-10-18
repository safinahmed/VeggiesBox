package com.veggiesbox.util;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.veggiesbox.model.UserAuthData;
import com.veggiesbox.model.UserLoginRequest;

public class MyAppFilter implements ContainerRequestFilter{


   public ContainerRequest filter(ContainerRequest request) {
	   
	  UserAuthData ulr = request.getEntity(UserAuthData.class);

      MultivaluedMap<String, String> headers = request.getRequestHeaders();

      headers.add("code", "MY_APP_CODE");
      request.setHeaders((InBoundHeaders)headers);

      return request;
   }
}