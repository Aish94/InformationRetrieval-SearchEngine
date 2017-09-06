import networkx as nx
fh=open("edgeList.txt", 'rb')
G = nx.read_edgelist(fh,create_using=nx.DiGraph()); #docID outgoinglink
fh.close();

print("Nodes: ");
print(G.number_of_nodes());
print("Edges: ");
print(G.number_of_edges());

pr = nx.pagerank(G, alpha=0.85, personalization=None, max_iter=30, tol=1e-06, nstart=None, weight='weight',dangling=None);
pageRankFile = open("external_pageRankFile.txt","w");
for node in pr:
    line = "solr-6.4.2/LATimesDownloadData/" + node + "=" + str(pr[node]) + "\n"
    pageRankFile.write(line);
pageRankFile.close();
