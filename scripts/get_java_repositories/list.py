import urllib.request
import json

auth = "https://api.github.com/?access_token="
tokens_ptr = 0
tokens = {0: "2cec86610286f8ed43ca08cace5b3b14374af183",
			1: "85567176f1d77ebe7792b85c36650ee30ef4f29e"}
client_id = "e24bed6b410a7bcc89a5"
client_secret = "80b111079f9e708da6ad463ccc8749152259d139"


def is_maven(contents_url):
	""" Check if given repository url is a gradle one.

		@contents_url	The REST url for the repository contents.
		@return			True iff it is a maven repository.
	"""
	req = urllib.request.Request(contents_url)
	req.add_header('Accept', 'application/json')
	json_res = urllib.request.urlopen(req).read()

	json_data = json_res.decode('utf-8')
	files = json.loads(json_data)

	for remote_file in files:
		if remote_file["name"] == "pom.xml":
			return True

	return False


def is_gradle(contents_url):
	""" Check if given repository url is a gradle one.

		@contents_url	The REST url for the repository contents.
		@return			True iff it is a gradle repository.
	"""
	req = urllib.request.Request(contents_url)
	req.add_header('Accept', 'application/json')
	json_response = urllib.request.urlopen(req).read()

	json_data = json_response.decode('utf-8')
	files = json.loads(json_data)

	for remote_file in files:
		if remote_file["name"] == "build.gradle":
			return True

	return False


def list_repositories(last):
	""" List repositories, starting from @last.

		@last:	The last listed repository id.

		@return Nothing.
	"""
	global tokens_ptr
	global client_id
	global client_secret
	java_hardcoded = "class=\"language-color\" aria-label=\"Java "

	try:
		print("Requesting https://api.github.com/repositories"
												+ "?since=" + str(last)
												+ "&client_id=" + client_id
												+ "&client_secret=" + client_secret)
		req = urllib.request.Request("https://api.github.com/repositories"
												+ "?since=" + str(last)
												+ "&client_id=" + client_id
												+ "&client_secret=" + client_secret)
		req.add_header('Accept', 'application/json')
		json_res = urllib.request.urlopen(req).read()
		json_data = json_res.decode('utf-8')
		data = json.loads(json_data)

		for repository in data:
			repository_id = repository["id"]
			git_url = repository["html_url"]
			github_page = git_url
			contents_url_tmp = str(repository["contents_url"]) + "?client_id=" + client_id	+ "&client_secret=" + client_secret
			contents_url = contents_url_tmp.replace("/{+path}", "")

			print("Repository: " + git_url)
			req = urllib.request.Request(github_page)
			github_page_response = urllib.request.urlopen(req).read()
			github_page_html = github_page_response.decode("utf-8")

			if java_hardcoded in github_page_html and ((is_maven(contents_url) or is_gradle(contents_url))):
				print("Adding " + str(git_url))
				open("repositories", "a").write(git_url + "\n")

			last = repository_id

		list_repositories(last)

	except urllib.error.HTTPError as e:
		if str(e).startswith("HTTP Error 403"):
			print("Switching to new token")
			tokens_ptr = tokens_ptr + 1

			req = urllib.request.Request(auth + tokens[tokens_ptr])
			urllib.request.urlopen(req).read()

			list_repositories(int(repository_id) - 1)
		else:
			print(str(e))
			list_repositories(int(repository_id) - 1)
	except UnicodeDecodeError:
		print("Decode error on " + str(git_url))
		list_repositories(int(last) + 1)

if __name__ == "__main__":
	req = urllib.request.Request(auth + tokens[tokens_ptr])
	urllib.request.urlopen(req).read()

	list_repositories(0)
