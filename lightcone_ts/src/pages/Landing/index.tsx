import React from 'react';
import { Button } from "@/components/ui/button"
import { Link } from 'react-router';

const LandingPage = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 px-4">
      <main className="text-center">
        <h1 className="text-4xl font-bold mb-4 text-gray-800">Lightcone</h1>
        <p className="text-xl mb-8 text-gray-600">Welcome to our innovative pre-alpha application</p>
        <div className="space-x-4">
          <Button asChild>
            <Link to="/login">Login</Link>
          </Button>
          <Button variant="outline" asChild>
            <Link to="/about">About / Sign Up</Link>
          </Button>
        </div>
      </main>
      <footer className="mt-8 text-sm text-gray-500">
        © {new Date().getFullYear()} RG025. All rights reserved.
      </footer>
    </div>
  )
}

export default LandingPage;
