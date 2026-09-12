import React from "react";
import ReactDOM from "react-dom";


type LoadingProps = {
  // message?: string;
  size?: number;
};

const LoadingScreen: React.FC<LoadingProps> = ({ size = 40 }) => {
  return ReactDOM.createPortal(
    <div
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        width: "100vw",
        height: "100vh",
        backgroundColor: "rgba(0, 0, 0, 0.5)",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        fontFamily: "Arial, sans-serif",
        color: "#fff",
        zIndex: 9999,
      }}>
      <svg
        width={size}
        height={size}
        viewBox="0 0 50 50"
        style={{ marginBottom: 12 }}
        xmlns="http://www.w3.org/2000/svg">
        <circle
          cx="25"
          cy="25"
          r="20"
          fill="none"
          stroke="#007bff"
          strokeWidth="5"
          strokeLinecap="round"
          strokeDasharray="90,150"
          strokeDashoffset="0">
          <animateTransform
            attributeName="transform"
            type="rotate"
            from="0 25 25"
            to="360 25 25"
            dur="1.2s"
            repeatCount="indefinite"/>
        </circle>
      </svg>
    </div>,
    document.body
  );
};

export default LoadingScreen;