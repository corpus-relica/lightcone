"use client";

import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useEffect, useState, useContext } from "react";
import Link from "next/link";
import { AuthContext } from '../AuthContext';
import { useRouter } from 'next/navigation';

const LoginPage = () => {
  const { isAuthenticated , login} = useContext(AuthContext);
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      router.push('/dashboard');
    }
  }, [isAuthenticated]);


  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");

    try {
      // Send login request to your Clojure backend
      const response = await fetch("http://localhost:3002/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      console.log("RESPONSE", response.ok)

      if (response.ok) {
        const data = await response.json();
        console.log("DATA", data)
        login(data.token);
        // Login successful, redirect to dashboard or update authentication state
      } else {
        // Login failed, display error message
        setError("Invalid username or password");
      }
    } catch (error) {
      setError("An error occurred. Please try again.");
      console.error(error);
    }

    setIsLoading(false);
  };

  return (
    <>
      <h1>Login</h1>
      <form onSubmit={handleSubmit}>
        <h4>Username</h4>
        <Input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <h4>Password</h4>
        <Input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        {error && <p className="error">{error}</p>}
        <Button type="submit" disabled={isLoading}>
          {isLoading ? "Loading..." : "Login"}
        </Button>
      </form>
      <p>
        Don't have an account? <Link href="/register">Register</Link>
      </p>
    </>
  );
};

export default LoginPage;
