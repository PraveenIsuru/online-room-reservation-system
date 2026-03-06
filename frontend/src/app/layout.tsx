'use client';

import { Inter } from "next/font/google";
import "./globals.css";
import { AuthProvider } from "../context/AuthContext";
import { usePathname } from "next/navigation";
import { useAuth } from "../context/AuthContext";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import Sidebar from "../components/Sidebar";
import Header from "../components/Header";

const inter = Inter({ subsets: ["latin"] });

function LayoutContent({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { user, loading } = useAuth();
  const router = useRouter();

  const isLoginPage = pathname === '/login';
  const isPublicPage = pathname === '/' || isLoginPage;

  useEffect(() => {
    if (!loading && !user && !isPublicPage) {
      router.push('/login');
    }
  }, [user, loading, router, isPublicPage]);

  if (loading && !isPublicPage) {
    return (
      <div className="h-screen w-screen flex items-center justify-center bg-gray-50">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#0B3D6E]"></div>
      </div>
    );
  }

  // Show login page without sidebar
  if (isPublicPage || !user) {
    return <>{children}</>;
  }

  // Show all other pages with sidebar
  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <div className="lg:pl-64">
        <Header />
        <main className="p-0">
          {children}
        </main>
      </div>
    </div>
  );
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <AuthProvider>
          <LayoutContent>{children}</LayoutContent>
        </AuthProvider>
      </body>
    </html>
  );
}
