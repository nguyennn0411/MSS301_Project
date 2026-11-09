import React from 'react';import ReactDOM from 'react-dom/client';import { BrowserRouter } from 'react-router-dom';import { ToastContainer } from 'react-toastify';import 'bootstrap/dist/css/bootstrap.min.css';import 'bootstrap-icons/font/bootstrap-icons.css';import 'react-toastify/dist/ReactToastify.css';import './App.css';import App from './App';import { AuthProvider } from './context/AuthContext';
// Keep the development server compatible with both classic and automatic JSX runtimes.
globalThis.React = React;

ReactDOM.createRoot(document.getElementById('root')).render(<React.StrictMode><BrowserRouter><AuthProvider><App/><ToastContainer position="top-right" autoClose={3500}/></AuthProvider></BrowserRouter></React.StrictMode>);
