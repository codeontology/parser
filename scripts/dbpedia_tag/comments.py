import sys
import urllib.request
import json

def tag(data):
	URIs = []
	try:
		for resource in data["Resources"]:
			if float(resource["@similarityScore"]) > 0.085:
				URIs.append(resource["@URI"])
	except KeyError:
		pass
	finally:
		return (data["@text"],URIs)


def tag_comments(comments_list, start, tries):
	res = []
	comments = comments_list[(int(start)):]
		
	max_tries = 8

	try:
		i = start
		for comment in comments:
			(URI, text) = (comment.split(" ")[0], " ".join(comment.split(" ")[1:]))
			print("URI: " + str(URI) + " - text: " + str(text))
			encoded_comment = urllib.parse.urlencode({"text" : text})
			print("comment" + str(encoded_commment))
			print("requesting http://spotlight.dbpedia.org/rest/annotate?" + encoded_comment + "&confidence=0.2&support=20")
			req = urllib.request.Request("http://spotlight.dbpedia.org/rest/annotate?" + encoded_comment
			  								+ "&confidence=0.2"
			  								+ "&support=20")
			req.add_header('Accept', 'application/json')
			json_res = urllib.request.urlopen(req).read()
			json_data = json_res.decode('utf-8')
			data = json.loads(json_data)
			(text, URIs)= tag(data)

			res.append(URIs)

			for about_URI in URIs:
				open('links.nt', 'a').write("<" + URI + ">"
						    	+ " <http://www.w3.org/2000/01/rdf-schema#about> "
							+ "<" + about_URI + "> ."'\n')
			i = int(i) + 1

	except Exception:			
		if tries < max_tries:
			tag_comments(comments_list, i, tries + 1)
		else:
			tag_comments(comments_list, i + 1, tries)



if __name__ == "__main__":
	comments_file = sys.argv[1]	
	with open(comments_file) as f:
		comments = f.readlines()

	tag_comments(comments, sys.argv[2], 0)
