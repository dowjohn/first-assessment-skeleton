export class Message {
  static fromJSON (buffer) {
    return new Message(JSON.parse(buffer.toString()))
  }

  constructor ({ username, command, contents }) {
    this.username = username
    this.command = command
    this.contents = contents
    this.timeStamp = new Date().toString()
  }

  toJSON () {
    return JSON.stringify({
      username: this.username,
      command: this.command,
      contents: this.contents,
      timeStamp: this.timeStamp
    })
  }

  toString () {
    switch (this.command) {
      case 'disconnect':
        return (`${this.timeStamp}: <${this.contents}> has disconnected`)
      case 'connect':
        return `${this.timeStamp}: <${this.contents}> has connected`
      case 'echo':
        return `${this.timeStamp} <${this.username}> (echo): ${this.contents}`
      case 'broadcast':
        return `${this.timeStamp} <${this.username}> (broadcast): ${this.contents}`
      case 'whisper':
        return `${this.timeStamp} <${this.username}> (whisper): ${this.contents}`
      case 'users':
        return `${this.timeStamp}: currently connected users: ${this.contents}`
      default:
        return `Something went wrong`
    }
  }
}
