#!/bin/bash

# Warehouse Management System - Rebuild Script
# Purpose: Stop running application, clean build, and rebuild JAR
# Platform: Windows Git Bash
# Usage: ./rebuild.sh

set -e  # Exit on error

echo "=================================================="
echo "  Warehouse Management System - Rebuild Script"
echo "=================================================="
echo ""

# Step 1: Kill any running Java processes on port 8080
echo "📍 Step 1: Stopping running application..."
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    PID=$(lsof -Pi :8080 -sTCP:LISTEN -t)
    echo "   Found running process on port 8080 (PID: $PID)"
    kill -9 $PID 2>/dev/null || true
    echo "   ✅ Process killed"
else
    echo "   ℹ️  No process running on port 8080"
fi

echo ""

# Step 2: Clean Maven build
echo "📍 Step 2: Cleaning Maven artifacts..."
mvn clean -q
if [ $? -eq 0 ]; then
    echo "   ✅ Clean successful"
else
    echo "   ❌ Clean failed"
    exit 1
fi

echo ""

# Step 3: Compile source code
echo "📍 Step 3: Compiling source code..."
mvn compile -q
if [ $? -eq 0 ]; then
    echo "   ✅ Compilation successful"
else
    echo "   ❌ Compilation failed"
    exit 1
fi

echo ""

# Step 4: Run tests
echo "📍 Step 4: Running tests..."
mvn test -q
if [ $? -eq 0 ]; then
    echo "   ✅ Tests passed"
else
    echo "   ⚠️  Some tests failed (continuing)"
fi

echo ""

# Step 5: Package JAR
echo "📍 Step 5: Building JAR package..."
mvn package -DskipTests=true -q
if [ $? -eq 0 ]; then
    echo "   ✅ Package created successfully"
else
    echo "   ❌ Package failed"
    exit 1
fi

echo ""

# Step 6: Verify JAR
echo "📍 Step 6: Verifying JAR file..."
if [ -f "target/warehouse-management-1.0.0.jar" ]; then
    JAR_SIZE=$(ls -lh target/warehouse-management-1.0.0.jar | awk '{print $5}')
    echo "   ✅ JAR found: target/warehouse-management-1.0.0.jar (Size: $JAR_SIZE)"
else
    echo "   ❌ JAR file not found"
    exit 1
fi

echo ""
echo "=================================================="
echo "  ✅ Build Complete!"
echo "=================================================="
echo ""
echo "Next steps:"
echo "  1. Start application:"
echo "     java -jar target/warehouse-management-1.0.0.jar"
echo ""
echo "  2. Access Swagger UI:"
echo "     http://localhost:8080/api/swagger-ui.html"
echo ""
echo "  3. Default credentials:"
echo "     Username: admin"
echo "     Password: admin123"
echo ""
echo "=================================================="

