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
	length = len(comments_list);
		
	max_tries = 8

	i = 0

	while (i < length):	
		try:
			comment = comments_list[i]
			(URI, text) = (comment.split(" ")[0], " ".join(comment.split(" ")[1:]))
			print("URI: " + str(URI) + " - text: " + str(text))
			encoded_comment = urllib.parse.urlencode({"text" : text})
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
			i = i + 1

		except Exception:	
			if tries < max_tries:
				i = i - 1
				tries = tries + 1



if __name__ == "__main__":
	comments_file = sys.argv[1]	
	with open(comments_file) as f:
		comments = f.readlines()

	tag_comments(comments, sys.argv[2], 0)
