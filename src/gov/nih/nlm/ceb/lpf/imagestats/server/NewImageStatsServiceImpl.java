package gov.nih.nlm.ceb.lpf.imagestats.server;

import gov.nih.nlm.ceb.lpf.imagestats.shared.ClientUtils;
import gov.nih.nlm.ceb.lpf.imagestats.shared.FacetModel;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ISConstants;
import gov.nih.nlm.ceb.lpf.imagestats.shared.ImageStatsException;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLPagingLoadResultBean;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLRecord;
import gov.nih.nlm.ceb.lpf.imagestats.shared.PLSolrParams;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class NewImageStatsServiceImpl extends RemoteServiceServlet {

	/*ArrayList<String> searchImagesFromEvents(ArrayList<String> SourceUrls){
		ArrayList<String> ret = new ArrayList<String>();
		Iterator<String> iturl = SourceUrls.iterator();
		while(iturl.hasNext()){
			String dUrl = (String)iturl.next();
			File dir = new File(dUrl);
			String[] subdir = dir.list();
			for(String s:subdir){
				if (new File(dUrl + s).isDirectory())
			    {
			        
			    }
			}
		}
		return ret;
	}*/
	
	public PLPagingLoadResultBean searchSOLRForPaging(String sourceServer, PLSolrParams searchParams) throws ImageStatsException {
		PLPagingLoadResultBean retpaging = null;
		int total = 0;
		List<PLRecord> recordList = new ArrayList<PLRecord>();
		Map <String, List<FacetModel>> facets = new LinkedHashMap<String, List<FacetModel>>();
		ArrayList<String> SourceUrls = new ArrayList<String>();
		SourceUrls.add(sourceServer);
		ArrayList<String> ret = new ArrayList<String>();
		Iterator<String> iturl = SourceUrls.iterator();
		while(iturl.hasNext()){
			String sUrl = (String)iturl.next();
			File dir = new File(sUrl);
			String[] subdir = dir.list();
			for(String s:subdir){
				File Event = new File(sUrl +"/"+ s);
				if (Event.isDirectory())
			    {
					ret.add(Event.getName());
					PLRecord record = new PLRecord();
					String[] files = Event.list();
					for(String file:files){
						//File f = new File(sUrl+"/"+s+"/"+file);
				        String type = "";
				        String subtype = "";
				        String mimetype=null;
				        try{
				        mimetype = Files.probeContentType(Paths.get(sUrl+s+"/"+file));}catch(IOException e){System.out.println(e.getMessage());}
				        if(mimetype!=null){
				        	type = mimetype.split("/")[0];
				        	
				        }
				        if(type!=null&&type.equals("image")){
				        	total ++;
				        	subtype = mimetype.split("/")[1];
				        	record.setName(file);
				        	record.setUrl(sUrl + "/" + s +"/" + file);
				        	record.setEventName(s);
				        	recordList.add(record);
				        }
					}
			    }
			}
		}
		ArrayList<FacetModel> facet = new ArrayList<FacetModel>();
		Iterator<String> it = ret.iterator();
		while(it.hasNext()){
			String event = it.next();
			facet.add(new FacetModel(event,ClientUtils.getFieldLabel(event)));
		}
		facets.put(ISConstants.FIELD_EVENT_NAME, facet);
		
		
		Set<String> v = searchParams.get("start");
		int start = 0;
	    if(v != null) {
	    	start = Integer.parseInt(v.iterator().next());
	    }
	    retpaging = new PLPagingLoadResultBean(recordList, total, start, facets);
		return retpaging;
	}

	
	public Map<String, List<FacetModel>> searchSOLRForEvents(String source, PLSolrParams searchParams) throws ImageStatsException {
		Map <String, List<FacetModel>> facets = new LinkedHashMap<String, List<FacetModel>>();
		ArrayList<String> SourceUrls = new ArrayList<String>();
		SourceUrls.add(source);
		ArrayList<String> ret = new ArrayList<String>();
		Iterator<String> iturl = SourceUrls.iterator();
		while(iturl.hasNext()){
			String sUrl = (String)iturl.next();
			File dir = new File(sUrl);
			String[] subdir = dir.list();
			for(String s:subdir){
				File Event = new File(sUrl + s);
				if (Event.isDirectory())
			    {
					ret.add(Event.getName());
			    }
			}
		}
		ArrayList<FacetModel> facet = new ArrayList<FacetModel>();
		Iterator<String> it = ret.iterator();
		while(it.hasNext()){
			String event = it.next();
			facet.add(new FacetModel(event,ClientUtils.getFieldLabel(event)));
		}
		facets.put(ISConstants.FIELD_EVENT_NAME, facet);
		return facets;
	}
}
