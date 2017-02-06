convert.time.since.epoch <- function(x) {
	as.POSIXct(x, origin = "1970-01-01", tz = "GMT")
}


if (!exists("movieLens")) {
	if (!file.exists("../resources/100k.dat")) {
		stop("because I can't download the data, I can't continue. You gotta download it manually and put it in the ../resources/ directory")
	} else {
		movieLens <- read.csv("../resources/100k.dat", header = F, sep = ",", stringsAsFactors = T, col.names = c("user_id","item_id","rating","epoch"), colClasses = c("numeric","numeric","numeric","numeric"))
		movieLens$timestamp <- convert.time.since.epoch(movieLens$epoch)
	}
}
