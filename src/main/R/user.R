if (!exists("movieLens")) {
	stop("master dataset is absent!")
}

user.list <- unique(sort(movieLens$user_id))

get.table.baseon.user_id <- function(uid) {
	movieLens[movieLens$user_id == uid,]
}

get.table.all.user_id <- function() {

}
