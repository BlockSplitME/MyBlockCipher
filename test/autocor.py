import matplotlib.pyplot as plt
import numpy as np

m = np.loadtxt("test/autoRoundFinal.txt")
plt.axis([-2000,2000,-1,1])
plt.title("Автокорреляция")
plt.plot(m[:,0], m[:,1], markersize=1)
plt.show()