import { useEffect, useRef } from "react";
import "@xterm/xterm/css/xterm.css"
import { Terminal as XTerminal } from "@xterm/xterm";
import { FitAddon } from "@xterm/addon-fit";

interface TerminalProps {
    socketUrl: string;
}
  
export const Terminal = ({ socketUrl }: TerminalProps) => {
  const terminalRef = useRef<HTMLDivElement>(null);
  const socketRef = useRef<WebSocket>(null);
  const inputBuffer = useRef<string>('');

  useEffect(() => {
    if (!terminalRef.current) return;


    const term = new XTerminal({
      cursorBlink: true,
      fontFamily: '"Fira Code", monospace',
      fontSize: 14,
      theme: { background: '#1e1e1e' },
    });

    const fitAddon = new FitAddon();
    term.loadAddon(fitAddon);
    term.open(terminalRef.current);
    fitAddon.fit();

    const socket = new WebSocket(socketUrl);
    socketRef.current = socket;



    window.onresize = function () {
      fitAddon.fit();
    };
  

    term.onResize((e) =>{
      const size = {
        type: "resize",
        cols: e.cols, 
        rows: e.rows
      }
      socket.send(JSON.stringify(size));
    })


    term.onData((data) => {
      inputBuffer.current += data;

      if (data.includes('\r') || data.includes('\n')) {
        socket.send(inputBuffer.current);
        inputBuffer.current = '';
      } else {
        setTimeout(() => {
          if (inputBuffer.current) {
            socket.send(inputBuffer.current);
            inputBuffer.current = '';
          }
        }, 50);
      }
    });


    term.onKey((event) => {
      if (event.domEvent.ctrlKey) {
        event.domEvent.preventDefault();
        switch (event.key) {
          case 'c':
            socket.send('\0x03');
            break;  // Ctrl+C
          case 'l':
            term.clear();
            break;
        }
      }
    });

    socket.onmessage = (event) => {
      term.write(event.data);
    };

    return () => {
      term.dispose();
      socket.close();
    };
  }, [socketUrl]);

  return <div ref={terminalRef} style={{ width: '100%', height: '100%' }} />;
};

export default Terminal