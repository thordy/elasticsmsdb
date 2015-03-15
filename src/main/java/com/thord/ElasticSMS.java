package com.thord;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Simple class that loads all messages from a given sms.db into Elasticsearch
 * @author thordy
 */
public class ElasticSMS {
	private static final String LOAD_MESSAGES_SQL = "SELECT"
			+ "   m.ROWID AS '_id',"
			+ "   m.guid,"
			+ "   m.text,"
			+ "   h.id AS 'from',"
			+ "   m.guid,"
			+ "   m.text,"
			+ "   m.replace,"
			+ "   m.service_center,"
			+ "   m.handle_id,"
			+ "   m.subject,"
			+ "   m.country,"
			+ "   m.version,"
			+ "   m.type,"
			+ "   m.service,"
			+ "   m.account,"
			+ "   m.account_guid,"
			+ "   m.error,"
			+ "   m.is_delivered,"
			+ "   m.is_finished,"
			+ "   m.is_emote,"
			+ "   m.is_from_me,"
			+ "   m.is_empty,"
			+ "   m.is_delayed,"
			+ "   m.is_auto_reply,"
			+ "   m.is_prepared,"
			+ "   m.is_read,"
			+ "   m.is_system_message,"
			+ "   m.is_forward,"
			+ "   m.was_downgraded,"
			+ "   m.is_archive,"
			+ "   m.cache_has_attachments,"
			+ "   m.cache_roomnames,"
			+ "   m.was_data_detected,"
			+ "   m.was_deduplicated,"
			+ "   m.is_audio_message,"
			+ "   m.is_played,"
			+ "   m.date_played,"
			+ "   m.item_type,"
			+ "   m.other_handle,"
			+ "   m.group_title,"
			+ "   m.group_action_type,"
			+ "   m.share_status,"
			+ "   m.share_direction,"
			+ "   m.is_expirable,"
			+ "   m.expire_state,"
			+ "   m.message_action_type,"
			+ "   m.message_source,"
			+ "   DATETIME(m.date + strftime('%s', '2001-01-01 00:00:00'), 'unixepoch', 'localtime') AS 'date',"
			+ "   DATETIME(m.date_read + strftime('%s', '2001-01-01 00:00:00'), 'unixepoch', 'localtime') AS 'date_read',"
			+ "   DATETIME(m.date_delivered + strftime('%s', '2001-01-01 00:00:00'), 'unixepoch', 'localtime') AS 'date_delivered'"
			+ " FROM message m, handle h"
			+ " WHERE"
			+ "   m.handle_id = h.ROWID";

	public static void main(String[] args) throws IOException {
		// Path to the sms.db file. This can also be a full path,
		// either '/full/path/to/file/sms.db' on OSX or 'C:\\\\files\\\\sms.db' on windows. Note
		// that you need to use four backslashes to escape things properly.
		String smsDb = "sms.db";

		String elasticHost = "localhost";
		int elasticPort = 9200;

		String elasticIndex = "messages";
		String type = "message";

		String mapping = ""
				+ "\"" + type + "\" : {"
				+ "	\"properties\" : {"
				+ "		\"date\" : {"
				+ "			\"type\" : \"date\","
				+ "			\"index\" : \"not_analyzed\","
				+ "			\"format\" : \"YYYY-MM-dd HH:mm:ss\""
				+ "		},"
				+ "		\"date_delivered\" : {"
				+ "			\"type\" : \"date\","
				+ "			\"index\" : \"not_analyzed\","
				+ "			\"format\" : \"YYYY-MM-dd HH:mm:ss\""
				+ "		},"
				+ "		\"date_played\" : {"
				+ "			\"type\" : \"date\","
				+ "			\"index\" : \"not_analyzed\","
				+ "			\"format\" : \"YYYY-MM-dd HH:mm:ss\""
				+ "		},"
				+ "		\"date_read\" : {"
				+ "			\"type\" : \"date\","
				+ "			\"index\" : \"not_analyzed\","
				+ "			\"format\" : \"YYYY-MM-dd HH:mm:ss\""
				+ "		},"
				+ "		\"from\" : {"
				+ "			\"type\" : \"string\","
				+ "			\"index\" : \"not_analyzed\""
				+ "		},"
				+ "		\"guid\" : {"
				+ "			\"type\" : \"string\","
				+ "			\"index\" : \"not_analyzed\""
				+ "		},"
				+ "		\"_id\" : {"
				+ "			\"type\" : \"integer\""
				+ "		}"
				+ "	}"
				+ "}";

		String jsonString = ""
				+ "{" +
				"	\"type\" : \"jdbc\"," +
				"	\"jdbc\" : {" + 
				"		\"url\" : \"jdbc:sqlite:" + smsDb + "\"," +
				"		\"user\" : \"\"," +
				"		\"password\" : \"\"," +
				"		\"sql\" : \"" + LOAD_MESSAGES_SQL + "\"," +
				"		\"index\" : \"" + elasticIndex + "\"," +
				"		\"type\" : \"" + type + "\"," +
				"		\"type_mapping\" : {" + mapping + "}" +
				"	}" +
				"}";
		StringEntity entity = new StringEntity(jsonString);

		HttpClient httpClient = HttpClientBuilder.create().build();

		String url = "http://" + elasticHost  + ":" + elasticPort + "/_river/" + elasticIndex + "/_meta";
		HttpPut putRequest = new HttpPut(url);

		putRequest.setEntity(entity);
		HttpResponse response = httpClient.execute(putRequest);

		System.out.println(response);
	}
}
