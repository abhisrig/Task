ImageLoader : Class exposed to clients. Class responsible for image fetching and cancelling request(Need to add cancelling feature yet).
Methods :
	
	1. initialize(Context context, int numOfThreads)
	numOfThreads arguments decide number of parallel network thread to run
	context is required for accessing resources

	2. with(final ImageView imageView, final String url) 
	used to display bitmap by fetching from provided url into the passed imageView

	3. getBitmapAsync(String url, ImageFetcher.ImageLoadCallback callback)
	bitmap fetched from network will be delivered to provided callback

Server Image : Widget extending ImageView, which helps to wrap server loaded bitmaps in an easy way. Client just need to call method bindImage with url of image to be fetched as arguments. If a client wants to display default bitmap while the actual image is being loaded can be easily configured by defining the property "app:defaultDrawableId". Error drawables can also be configured, in case image retrieval fails from server by defining propery "errorDrawableId" in xml.

ImageFetcher : Class not exposed to client and has package level visibility. 
Is responsible for initializing the basic infrastructure for image loading framework. Initialized cache, cacheQueue, networkQueue, cacheThread, and imageRequestThread.
