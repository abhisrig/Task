ImageLoader : Class exposed to clients. Class responsible for image fetching and cancelling request(Need to add cancelling feature yet).
Methods :
	
	1. initialize(Context context, int numOfThreads)
	numOfThreads arguments decide number of parallel network thread to run
	context is required for accessing resources

	2. with(final ImageView imageView, final String url) 
	used to display bitmap by fetching from provided url into the passed imageView

	3. getBitmapAsync(String url, ImageFetcher.ImageLoadCallback callback)
	bitmap fetched from network will be delivered to provided callback

Server Image : Widget extending ImageView, which helps to wrap server loaded bitmaps in an easy way. Client need to call method 
method bindImage(ImageView image, String url) with url of image to be fetched as arguments and imageView instance on which bitmap needs to be displayed. If a client wants to display default bitmap while the actual image is being loaded can be easily configured by defining the property "app:defaultDrawableId". Error drawables can also be configured, in case image retrieval fails from server by defining propery "errorDrawableId" in xml.

ImageFetcher : Class not exposed to client and has package level visibility. 
Is responsible for initializing the basic infrastructure for image loading framework. Initialized cache, cacheQueue, networkQueue, cacheThread, and imageRequestThread. Member variables are as follows
Map of request : used to handle current processing requests
Queue of request in Cache, cacheQueue : keep tracks of requests which are going to be served by CacheThread
Queue of request in Network, mRequestQueue : keep tracks of requests which are going to be serve by Network Thread;
Cache, cache : DiskbasedCache implementation, to provide cache data from disk.

ImageRequestThread has its own queue(provided by ImageFetcher) and cache object(provided by ImageFetcher)
CacheThread has its own queue, queue of ImageRequestThread and cache object.

When a request is initiated, imageFetcher does following
1. Check whether the request is in requestMap, if its present in requestMap, it adds the callback to the list of ImageRequest.
2. If its not present in requestMap, it adds the request in requestmap and cacheQueue.
3. CacheThreads picks the request from its own queue, and check for its cacheEntry. If a cache entry is found and is not expired, result is delivered from cache.
4. If cache entry is not found, CacheThread adds the request in ImageRequestThread's queue.
5. ImageRequestThread picks the request and execute it and cache the result using the cache object, and delivers the result back to caller.
Above steps are repeated.

TradeOffs: 
Cache directory and cache size are hardcoded as of now.
Cache softexpiry and full expiry is hardcoded. Need to include mechanism to respect server defined cache controls.
ImageRequest object needs to be generalized.

