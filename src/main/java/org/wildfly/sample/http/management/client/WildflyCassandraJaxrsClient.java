/*
 * Copyright (C) 2014 Red Hat, inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.wildfly.sample.http.management.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2014 Red Hat, inc.
 */
public class WildflyCassandraJaxrsClient {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {        
        String host = args.length > 0 && args[0] != null ? args[0].trim() : "localhost";
        Client client = configureJaxrsClient();
        String operation = "{\"operation\":\"read-resource\",\"recursive\":\"true\",\"resolve-expressions\":\"true\",\"address\":[\"subsystem\",\"cassandra\",\"cluster\",\"WildflyCluster\"]}";
        System.out.println("Response " + prettyPrintJson( executeQuery(client, host, operation)));
    }
    private static Client configureJaxrsClient() {
        return  ClientBuilder.newClient().register(HttpAuthenticationFeature.digest("admin", "passw0rd!"));
    }
    
    private static String executeQuery(Client client, String host, String operationJson) throws IOException {        
        System.out.println("Query " + prettyPrintJson(operationJson));
        WebTarget target = client.target(String.format("http://%s:9990/management", host));
        return target.request(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).post(Entity.json(operationJson), String.class);
    }
    
    private static String prettyPrintJson(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(jsonString, Object.class));
    }
}
