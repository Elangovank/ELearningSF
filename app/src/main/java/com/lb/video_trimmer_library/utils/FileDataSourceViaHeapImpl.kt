package com.gm.videoTrimmer.utils

import com.googlecode.mp4parser.DataSource
import com.googlecode.mp4parser.util.Logger

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.WritableByteChannel

import com.googlecode.mp4parser.util.CastUtils.l2i


class FileDataSourceViaHeapImpl : DataSource {
    internal var fc: FileChannel
    internal var filename: String


    @Throws(FileNotFoundException::class)
    constructor(f: File) {
        this.fc = FileInputStream(f).channel
        this.filename = f.name
    }

    @Throws(FileNotFoundException::class)
    constructor(f: String) {
        val file = File(f)
        this.fc = FileInputStream(file).channel
        this.filename = file.name
    }


    constructor(fc: FileChannel) {
        this.fc = fc
        this.filename = "unknown"
    }

    constructor(fc: FileChannel, filename: String) {
        this.fc = fc
        this.filename = filename
    }

    @Synchronized
    @Throws(IOException::class)
    override fun read(byteBuffer: ByteBuffer): Int {
        return fc.read(byteBuffer)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun size(): Long {
        return fc.size()
    }

    @Synchronized
    @Throws(IOException::class)
    override fun position(): Long {
        return fc.position()
    }

    @Synchronized
    @Throws(IOException::class)
    override fun position(nuPos: Long) {
        fc.position(nuPos)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun transferTo(startPosition: Long, count: Long, sink: WritableByteChannel): Long {
        return fc.transferTo(startPosition, count, sink)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun map(startPosition: Long, size: Long): ByteBuffer {
        val bb = ByteBuffer.allocate(l2i(size))
        fc.read(bb, startPosition)
        return bb.rewind() as ByteBuffer
    }

    @Throws(IOException::class)
    override fun close() {
        fc.close()
    }

    override fun toString(): String {
        return filename
    }

    companion object {
        private val LOG = Logger.getLogger(FileDataSourceViaHeapImpl::class.java)
    }

}