/* MessagesDataSource.java

   Copyright (c) 2009 Juergen Schlierf, All Rights Reserved
   
   This file is part of Cubusmail (http://code.google.com/p/cubusmail/).
	
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.
	
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
	
   You should have received a copy of the GNU Lesser General Public
   License along with Cubusmail. If not, see <http://www.gnu.org/licenses/>.
 */
package com.cubusmail.client.datasource;

import com.cubusmail.client.exceptions.GWTExceptionHandler;
import com.cubusmail.client.util.ServiceProvider;
import com.cubusmail.common.model.GWTMessageList;
import com.cubusmail.common.model.MessageListFields;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Data source for all messages.
 * 
 * @author Juergen Schlierf
 */
public class MessageListDataSource extends GwtRpcDataSource {

	public MessageListDataSource() {

		for (MessageListFields fieldDef : MessageListFields.values()) {
			DataSourceField field = new DataSourceIntegerField( fieldDef.name() );
			if ( MessageListFields.ID.equals( fieldDef ) ) {
				field.setRequired( true );
				field.setPrimaryKey( true );
			}
			addField( field );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.client.datasource.GwtRpcDataSource#executeAdd(java.lang
	 * .String, com.smartgwt.client.data.DSRequest,
	 * com.smartgwt.client.data.DSResponse)
	 */
	@Override
	protected void executeAdd( String requestId, DSRequest request, final DSResponse response ) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.client.datasource.GwtRpcDataSource#executeFetch(java.lang
	 * .String, com.smartgwt.client.data.DSRequest,
	 * com.smartgwt.client.data.DSResponse)
	 */
	@Override
	protected void executeFetch( final String requestId, DSRequest request, final DSResponse response ) {

		final int startIndex = (request.getStartRow() < 0) ? 0 : request.getStartRow();
		final int endIndex = (request.getEndRow() == null) ? -1 : request.getEndRow();

		ServiceProvider.getMailboxService().retrieveMessages( "INBOX", startIndex, endIndex, "", "", new String[2][0],
				new AsyncCallback<GWTMessageList>() {

					public void onSuccess( GWTMessageList result ) {

						mapResponse( response, result );
						processResponse( requestId, response );
					}

					public void onFailure( Throwable caught ) {

						GWTExceptionHandler.handleException( caught );
						response.setStatus( RPCResponse.STATUS_FAILURE );
						processResponse( requestId, response );
					}
				} );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.client.datasource.GwtRpcDataSource#executeRemove(java.lang
	 * .String, com.smartgwt.client.data.DSRequest,
	 * com.smartgwt.client.data.DSResponse)
	 */
	@Override
	protected void executeRemove( String requestId, DSRequest request, DSResponse response ) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.client.datasource.GwtRpcDataSource#executeUpdate(java.lang
	 * .String, com.smartgwt.client.data.DSRequest,
	 * com.smartgwt.client.data.DSResponse)
	 */
	@Override
	protected void executeUpdate( String requestId, DSRequest request, DSResponse response ) {

	}

	/**
	 * @param response
	 * @param data
	 */
	private void mapResponse( DSResponse response, GWTMessageList data ) {

		if ( data != null && data.getTotalRecords() > 0 ) {
			ListGridRecord[] records = new ListGridRecord[data.getTotalRecords()];
			for (int i = 0; i < data.getTotalRecords(); i++) {
				String[] source = data.getMessages()[i];
				records[i] = new ListGridRecord();
				for (MessageListFields fieldDef : MessageListFields.values()) {
					records[i].setAttribute( fieldDef.name(), source[fieldDef.ordinal()] );
				}
			}
			response.setData( records );
		}
	}
}