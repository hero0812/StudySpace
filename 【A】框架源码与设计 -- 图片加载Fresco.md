## 【A】框架源码与设计 -- 图片加载框架 Fresco



Android 4.x时代 用NativeMemoryPool

NativePoolByteBufferFactory 生成

NativeMemorryChunkPool

具体使用NativeMemoryChunk在直接内存上操作Bitmap 



Android L以上

只能老老实实用 BitmapPool

ArtBitmapFactory 生成 BitmapPool

BitmapPool里边只能老老实实用Bitmap提供的createBitmap、recycle方法。











