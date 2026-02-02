#!/bin/bash

# =============================================================================
# AI BASICS - TEST ALL ENDPOINTS
# =============================================================================
# This script tests all endpoints in the Spring AI educational application.
# Run this after starting the application with: ./gradlew bootRun
# =============================================================================

BASE_URL="http://localhost:8080"

# Colors for output (works on most terminals)
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() {
    echo ""
    echo -e "${BLUE}==================================================================${NC}"
    echo -e "${YELLOW}$1${NC}"
    echo -e "${BLUE}==================================================================${NC}"
    echo ""
}

print_subheader() {
    echo ""
    echo -e "${GREEN}--- $1 ---${NC}"
    echo ""
}

# Check if the server is running
print_header "🔍 CHECKING SERVER STATUS"
if curl -s "$BASE_URL/basic-prompt/hello" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Server is running at $BASE_URL${NC}"
else
    echo -e "${RED}❌ Server is not running. Start it with: ./gradlew bootRun${NC}"
    exit 1
fi

# =============================================================================
# PART 2: BASIC PROMPTS
# =============================================================================
print_header "📝 PART 2: BASIC PROMPTS"

print_subheader "2.1 Hello AI"
curl -s "$BASE_URL/basic-prompt/hello"
echo ""

print_subheader "2.2 Custom Prompt"
curl -s "$BASE_URL/basic-prompt/ask?prompt=What%20is%20the%20capital%20of%20Japan?"
echo ""

print_subheader "2.3 With System Prompt"
curl -s "$BASE_URL/basic-prompt/with-system?prompt=What%20is%20a%20variable?"
echo ""

# =============================================================================
# PART 3: PROMPT TEMPLATES
# =============================================================================
print_header "🎨 PART 3: PROMPT TEMPLATES"

print_subheader "3.1 Bad vs Good Prompt"
curl -s "$BASE_URL/prompt-templates/compare?topic=loops"
echo ""

print_subheader "3.2 Template with Variables"
curl -s "$BASE_URL/prompt-templates/explain?topic=functions&audience=teenager&style=casual"
echo ""

print_subheader "3.3 Role-Based Prompt"
curl -s "$BASE_URL/prompt-templates/role?role=chef&question=What%20is%20an%20algorithm?"
echo ""

# =============================================================================
# PART 4: GENERATION PARAMETERS
# =============================================================================
print_header "🌡️  PART 4: GENERATION PARAMETERS"

print_subheader "4.1 Temperature Comparison"
curl -s "$BASE_URL/generation-params/temperature-compare"
echo ""

print_subheader "4.2 Deterministic Demo"
curl -s "$BASE_URL/generation-params/deterministic?runs=2"
echo ""

# =============================================================================
# PART 5: EMBEDDINGS
# =============================================================================
print_header "📊 PART 5: EMBEDDINGS"

print_subheader "5.1 Generate Embedding"
curl -s "$BASE_URL/embeddings/generate?text=Machine%20learning%20is%20fascinating"
echo ""

print_subheader "5.2 Compare Similarity (Similar texts)"
curl -s "$BASE_URL/embeddings/compare?text1=I%20love%20programming&text2=Coding%20is%20my%20passion"
echo ""

print_subheader "5.3 Compare Similarity (Different texts)"
curl -s "$BASE_URL/embeddings/compare?text1=I%20love%20programming&text2=The%20weather%20is%20sunny"
echo ""

# =============================================================================
# PART 6: CONTEXT WINDOW
# =============================================================================
print_header "📏 PART 6: CONTEXT WINDOW"

print_subheader "6.1 Context Retention"
curl -s "$BASE_URL/context-window/retention-test"
echo ""

print_subheader "6.2 Token Estimation"
curl -s "$BASE_URL/context-window/estimate-tokens?text=Spring%20AI%20is%20a%20great%20framework%20for%20building%20AI%20applications"
echo ""

# =============================================================================
# PART 7: STUDY ASSISTANT (RAG)
# =============================================================================
print_header "🎓 PART 7: STUDY ASSISTANT (RAG)"

print_subheader "7.1 Add Study Notes"
curl -s -X POST "$BASE_URL/study-assistant/notes" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": [
      "Photosynthesis is the process by which plants convert sunlight, water, and CO2 into glucose and oxygen. It occurs in the chloroplasts.",
      "The mitochondria is the powerhouse of the cell. It produces ATP through cellular respiration.",
      "DNA stands for deoxyribonucleic acid. It contains the genetic instructions for all living organisms.",
      "The water cycle includes evaporation, condensation, precipitation, and collection."
    ]
  }'
echo ""

print_subheader "7.2 Check Status"
curl -s "$BASE_URL/study-assistant/status"
echo ""

print_subheader "7.3 Ask Question"
curl -s -X POST "$BASE_URL/study-assistant/ask" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What produces energy in cells?",
    "topK": 2,
    "temperature": 0.7
  }'
echo ""

print_subheader "7.4 Compare RAG vs No RAG"
curl -s -X POST "$BASE_URL/study-assistant/compare-rag" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is photosynthesis?",
    "customContext": "We also learned it uses chlorophyll."
  }'
echo ""

print_subheader "7.5 Clear Notes"
curl -s -X DELETE "$BASE_URL/study-assistant/notes"
echo ""

# =============================================================================
# SUMMARY
# =============================================================================
print_header "✅ ALL TESTS COMPLETED"

echo "All endpoints were tested successfully!"
echo ""
echo "Next steps:"
echo "1. Try modifying parameters in the curl commands"
echo "2. Read the code comments to understand each concept"
echo "3. Build your own variations of these examples"
echo ""
