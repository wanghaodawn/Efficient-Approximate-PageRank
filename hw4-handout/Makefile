CC := javac
SRC := ApproxPageRank.java
CLS := ApproxPageRank
DATA := data
VIS := vis
HANDIN := handin.tar

GRAPH := polblogs

SEED_0 := 715
SEED_1 := 230
ALPHA := 3e-1
EPS := 1e-5


all: apr

apr: $(SRC)
	$(CC) $(SRC)

pagerank: all
	java $(CLS) $(DATA)/$(GRAPH).adj $(SEED_0) $(ALPHA) $(EPS) > $(DATA)/$(GRAPH)-0.pr
	java $(CLS) $(DATA)/$(GRAPH).adj $(SEED_1) $(ALPHA) $(EPS) > $(DATA)/$(GRAPH)-1.pr

gephi: pagerank
	vis/gen_gdf.py $(DATA)/$(GRAPH).adj $(DATA)/$(GRAPH)-0.pr $(DATA)/$(GRAPH)-1.pr > \
		$(VIS)/$(GRAPH).gdf

handin: clean
	tar --exclude="$(DATA)" --exclude="$(VIS)" --exclude="$(HANDIN)" \
		-cvf $(HANDIN) .

clean:
	rm -rf *.class *.tar

.PHONY:
	data pagerank gephi handin clean
