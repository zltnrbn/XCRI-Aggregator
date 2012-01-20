package com.k_int.xcri

import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*

class SearchController {


  def ESWrapperService

  def index() { 
    // log.debug("Search Index, params.coursetitle=${params.coursetitle}, params.coursedescription=${params.coursedescription}, params.freetext=${params.freetext}")
    log.debug("Search Index, params.q=${params.q}")

    def pagename = 'index'
    def result = [:]

    // Get hold of some services we might use ;)
    def mongo = new com.gmongo.GMongo();
    def db = mongo.getDB("xcri")
    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()

    if ( params.q != null ) {

      def query_terms = params.q
	  params.max = Math.min(params.max ? params.int('max') : 10, 100)
	  params.offset = params.offset ? params.int('offset') : 0
	  
      if ( query_terms=='' )
        query_terms="*"

      // def b = builder.buildAsString {
		
      def search_closure = {
        source {
          from = params.offset
          size = params.max
		  if(params.subject)
		  {
			  query
			  {
				  filtered
				  {  
					  query
					  {	  
						query_string (query: query_terms)
					  }
					  if(params.subject)
					  {
						  filter
						  {
							 and [{
								 if(params.subject)
								 {
									term 
									{
										subject = params.subject
									}
								 }
								 if(params.provider)
								 {
									term
									{
										provid = params.provider 
									}
								 }
							 }]
						  }
					  }
				  }
			  }
			  facets
			  {
				subject
				{
				  terms
				  {
					field = 'subject'
				  }
				}
				provider
				{
				  terms
				  {
					field = 'provid'
				  }
				}
			  }
		  }
		  else
		  {
			  query
			  { 
				query_string (query: query_terms)
			  }
			  facets
			  {
				subject
				{
				  terms 
				  {
					field = 'subject'
				  }
				}
				provider 
				{
				  terms 
				  {
					field = 'provid'
				  }
				}
			  }
		  	}
          }
      }

      def search = esclient.search(search_closure)

      //      and : [
      //        params.coursetitle ?: { term(title:params.coursetitle) },
      //        params.coursedescription ?: { term(descriptions:params.coursedescription) },
      //        params.freetext ?: { term(params.freetext) },
      //      ]

      println "Search returned $search.response.hits.totalHits total hits"
      println "First hit course is $search.response.hits[0]"
      result.searchresult = search.response
      result.resultsTotal = search.response.hits.totalHits
      result.facets = search.response.facets?.facets

      pagename='results'
      // render(view:'results',model:result) 
    }
    else {
      log.debug("No query.. Show search page")
      // render(view:'index',model:result)
    }

    withFormat {
      html {
        render(view:pagename,model:result)
      }
      xml {
        render result as XML
      }
      json {
        render result as JSON
      }
    }
  }

  def testSearchClosurei(c) {
    log.debug("testSearchClosure");
    def builder = new GXContentBuilder()
    def b = builder.buildAsString(c)
    log.debug(b.toString())
  }
}
