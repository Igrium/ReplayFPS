# Client-Capture File Specification

## Storage

A client-capture, or `.ccap` file, is stored within the replay archive (`.mcpr`) alongside the packet data. The zip entry it is stored under must be named `client.ccap`. These files are accessed from within the replay archive, and they should generally only be extracted for debugging purposes.

If the file is missing, or named something other than `client.ccap`, the Replay FPS addon will be disabled and replays will be played using only packet data.

## Header

Every client-capture begins with a header declaring vital metadata pertaining to the file. The rest of the file is unreadable without the header.

The header is made of an *uncompressed* [Binary NBT](https://minecraft.wiki/w/NBT_format#Binary_format) tag. As the `mcpr` file is already compressed, any additional compression at this level is unnecessary. The NBT schema is as follows:

- `[root]: TAG_Compound`
  
  - `channels: TAG_List`: All the data channels within the file (see below).
    
    - `TAG_Compound`: A channel declaration.
      
      - `id: TAG_String`: The namespaced identifier of the channel type. (ex. "`replayfps:player_pos`")
      
      - `size: TAG_Int`: The number of bytes this channel will use in every frame.
  
  - `framerate: TAG_Int` The numerator of the file's frame rate.
  
  - `framerateBase: TAG_Int` The denominator of the file's frame rate.
  
  - `localPlayerID: TAG_Int` The network id of the player that recorded this replay.

The byte length of the header should be recorded, as it is used while determining frame offsets.

### Channels

Every client-capture has a set number of "channels", declared in a specific order. Each channel defines a specific attribute of the local player that will be captured and replayed each frame. For instance `replayfps:player_pos` could save the player's world position as 3 doubles.

A channel's behavior is determined by its *type*, defined by the `id` tag in its declaration. A channel type controls how the data in a channel is written, and subsequently read and applied to the game. Without the type, a data is a meaningless set of bytes. This file specification defines no individual channel types, as it pertains only to the overall file structure.

*Only one channel of a given type may be defined per-file.*

Every channel declaration also contains a `size` tag. While each channel type should know how many bytes it ought to read, in the case that a given type is not found, the program must know how many bytes to discard. If the `size` tag does not match the channel type's expected size, the channel is discarded and treated as if the type was not found.

Every channel must be present on every frame.

## Frame Data

The rest of the file holds the actual data stream. It is designed such that frames can be streamed & buffered from disk on an as-needed basis, without the need to hold the entire file in memory.

After the header, the file is comprised of serialized frame data, one after the other for as many frames as there are. There is no delimiter notating the start or end of each frame, however, the length of each frame (which is consistent throughout the entire file) can be calculated by taking the sum of every channel's size.

The data within the frame itself is comprised of the raw bytes written by each channel, in the order in which they were declared in the header. Like with frames, there is no delimiter notating where each channel starts or ends, hence the need for every channel to declare its size ahead of time.

A simple client-capture with 3 channels would look something like:

```
header | [channel 1] [channel 2] [channel 3] | [channel 1] [channel 2] [channel 3] | ...
```

Due to the intentional lack of delimiters and consistent frame size, the byte offset of any given frame can be calculated with:

```
offset = index * frame_size + header_length
```

This allows for easy frame seeking without the need to index the file.

The major drawback of this approach, however, is that there could be wasted space when channels go unused for a given frame. But as long as the "default" value remains consistent, the zip compression of the `mcpr` file should mitigate that.
