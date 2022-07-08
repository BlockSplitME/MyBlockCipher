import matplotlib.pyplot as plt
import numpy as np

m = np.loadtxt("test/resheto.txt")
plt.axis([0,255,0,255])
plt.title("Тест на решетчатость")
plt.plot(m[:,0], m[:,1], 'o', markersize=1)
plt.show()